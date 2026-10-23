package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.pojo.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体库业务接口。
 */
public interface MediaService {

    /**
     * 上传图片到媒体库（最大 40MB）。
     */
    Media upload(MultipartFile file);

    /**
     * 上传附加内容附件到媒体库（最大 40MB，支持文档 / 压缩包等常见文件）。
     */
    Media uploadFile(MultipartFile file);

    /**
     * 分页查询媒体库。
     */
    PageResult<Media> page(Integer page, Integer pageSize);

    /**
     * 删除媒体（同时删除磁盘文件）。
     */
    void delete(Long id);

    /**
     * 读取媒体库文件（用于下载），文件不存在时抛出异常。
     */
    FileSystemResource loadResource(Long id);
}
