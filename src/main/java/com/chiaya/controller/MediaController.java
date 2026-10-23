package com.chiaya.controller;

import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.pojo.Media;
import com.chiaya.service.MediaService;
import com.chiaya.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体库接口（图片上传 / 列表 / 删除）。
 */
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    private final PermissionService permissionService;

    public MediaController(MediaService mediaService, PermissionService permissionService) {
        this.mediaService = mediaService;
        this.permissionService = permissionService;
    }

    /** 上传图片（最大 40MB） POST /api/media/upload */
    @PostMapping("/upload")
    public ResponseEntity<Result<Media>> upload(@RequestParam("file") MultipartFile file) {
        permissionService.requireArticleWrite();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("上传成功", mediaService.upload(file)));
    }

    /** 上传附加内容附件（最大 40MB） POST /api/media/files */
    @PostMapping("/files")
    public ResponseEntity<Result<Media>> uploadFile(@RequestParam("file") MultipartFile file) {
        permissionService.requireArticleWrite();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("文件上传成功", mediaService.uploadFile(file)));
    }

    /** 分页查询媒体库 GET /api/media?page=1&pageSize=24 */
    @GetMapping
    public Result<PageResult<Media>> page(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "24") Integer pageSize) {
        permissionService.requireArticleWrite();
        return Result.success(mediaService.page(page, pageSize));
    }

    /** 删除媒体 DELETE /api/media/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        mediaService.delete(id);
        return Result.success("图片已删除", null);
    }
}
