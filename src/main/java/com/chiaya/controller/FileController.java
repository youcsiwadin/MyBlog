package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.pojo.Media;
import com.chiaya.service.MediaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口：站点主题设置中的封面 / 头像图片上传（同时登记到媒体库）。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final MediaService mediaService;

    public FileController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * 上传图片，返回可直接访问的地址。
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        Media media = mediaService.upload(file);
        return Result.success("上传成功", media.getUrl());
    }
}
