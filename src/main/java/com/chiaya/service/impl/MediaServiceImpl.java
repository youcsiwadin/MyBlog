package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.context.UserContext;
import com.chiaya.dao.MediaDao;
import com.chiaya.pojo.Media;
import com.chiaya.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 媒体库业务实现：图片保存到本地 uploads 目录，并在 media 表中登记。
 */
@Service
public class MediaServiceImpl implements MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaServiceImpl.class);

    /** 媒体库图片上传允许的格式 */
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp", "bmp");

    /** 附加内容附件允许的格式（文档、压缩包、图片、音视频等常见文件） */
    private static final Set<String> FILE_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "csv", "json", "xml",
            "zip", "rar", "7z", "tar", "gz",
            "png", "jpg", "jpeg", "gif", "webp", "bmp",
            "mp3", "wav", "mp4", "mov");

    /** 单个文件最大 40MB */
    private static final long MAX_SIZE = 40L * 1024 * 1024;

    private static final int DEFAULT_PAGE_SIZE = 24;

    private static final int MAX_PAGE_SIZE = 100;

    private static final DateTimeFormatter FOLDER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    private static final String URL_PREFIX = "/uploads/";

    private final MediaDao mediaDao;

    private final Path uploadRoot;

    public MediaServiceImpl(MediaDao mediaDao, @Value("${blog.upload.dir:uploads}") String uploadDir) {
        this.mediaDao = mediaDao;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public Media upload(MultipartFile file) {
        return store(file, IMAGE_EXTENSIONS, "图片",
                "仅支持 png、jpg、jpeg、gif、webp、bmp 格式的图片");
    }

    @Override
    public Media uploadFile(MultipartFile file) {
        return store(file, FILE_EXTENSIONS, "文件",
                "不支持该文件格式，请上传文档、压缩包、图片或音视频文件");
    }

    /**
     * 保存文件到 uploads 目录并在 media 表中登记。
     *
     * @param file            上传的文件
     * @param allowed         允许的扩展名（小写，不含点）
     * @param kindLabel       用于提示文案的资源类型名称（图片 / 文件）
     * @param formatMessage   格式不合法时的提示
     */
    private Media store(MultipartFile file, Set<String> allowed, String kindLabel, String formatMessage) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("请选择要上传的" + kindLabel);
        }
        if (file.getSize() > MAX_SIZE) {
            throw BusinessException.badRequest(kindLabel + "大小不能超过 40MB");
        }
        String extension = resolveExtension(file.getOriginalFilename());
        if (!allowed.contains(extension)) {
            throw BusinessException.badRequest(formatMessage);
        }
        String folder = LocalDate.now().format(FOLDER_FORMATTER);
        Path targetFolder = uploadRoot.resolve(folder).normalize();
        if (!targetFolder.startsWith(uploadRoot)) {
            throw BusinessException.badRequest("非法的上传路径");
        }
        try {
            Files.createDirectories(targetFolder);
            String suffix = extension.isEmpty() ? "" : "." + extension;
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path target = targetFolder.resolve(fileName).normalize();
            if (!target.startsWith(uploadRoot)) {
                throw BusinessException.badRequest("非法的上传路径");
            }
            file.transferTo(target.toFile());

            Media media = new Media();
            media.setFileName(resolveOriginalName(file.getOriginalFilename()));
            media.setUrl(URL_PREFIX + folder + "/" + fileName);
            media.setSize(file.getSize());
            media.setMimeType(file.getContentType() == null ? "" : file.getContentType());
            media.setUploaderId(UserContext.getUserId());
            mediaDao.insert(media);
            return mediaDao.findById(media.getId());
        } catch (IOException ex) {
            log.error("{}保存失败", kindLabel, ex);
            throw new BusinessException(500, kindLabel + "保存失败，请稍后重试");
        }
    }

    @Override
    public PageResult<Media> page(Integer page, Integer pageSize) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        long total = mediaDao.countAll();
        List<Media> records = total == 0 ? List.of() : mediaDao.pageList((currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public void delete(Long id) {
        Media media = mediaDao.findById(id);
        if (media == null) {
            throw BusinessException.notFound("图片不存在或已被删除");
        }
        mediaDao.deleteById(id);
        // 同步删除磁盘文件
        String url = media.getUrl();
        if (url != null && url.startsWith(URL_PREFIX)) {
            try {
                Path target = uploadRoot.resolve(url.substring(URL_PREFIX.length())).normalize();
                if (target.startsWith(uploadRoot)) {
                    Files.deleteIfExists(target);
                }
            } catch (IOException ex) {
                log.warn("媒体文件删除失败：{}", url, ex);
            }
        }
    }

    @Override
    public FileSystemResource loadResource(Long id) {
        Media media = mediaDao.findById(id);
        if (media == null) {
            throw BusinessException.notFound("文件不存在或已被删除");
        }
        String url = media.getUrl();
        if (url == null || !url.startsWith(URL_PREFIX)) {
            throw BusinessException.notFound("文件不存在或已被删除");
        }
        Path target = uploadRoot.resolve(url.substring(URL_PREFIX.length())).normalize();
        if (!target.startsWith(uploadRoot) || !Files.isRegularFile(target)) {
            throw BusinessException.notFound("文件不存在或已被删除");
        }
        return new FileSystemResource(target);
    }

    private String resolveExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int index = originalFilename.lastIndexOf('.');
        if (index < 0 || index == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveOriginalName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "未命名文件";
        }
        String name = originalFilename.replace('\\', '/');
        int index = name.lastIndexOf('/');
        name = index >= 0 ? name.substring(index + 1) : name;
        return name.length() > 255 ? name.substring(name.length() - 255) : name;
    }
}
