package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体库资源（图片），对应数据表 media。
 */
@Data
public class Media {

    private Long id;

    /** 原始文件名 */
    private String fileName;

    /** 访问地址 */
    private String url;

    /** 文件大小（字节） */
    private Long size;

    /** 文件类型 */
    private String mimeType;

    /** 上传用户ID */
    private Long uploaderId;

    private LocalDateTime createTime;
}
