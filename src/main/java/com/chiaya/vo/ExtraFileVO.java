package com.chiaya.vo;

import lombok.Data;

/**
 * 文章附加内容附件（关联媒体库文件）。
 */
@Data
public class ExtraFileVO {

    /** 媒体库文件ID（关联 media.id） */
    private Long mediaId;

    /** 原始文件名 */
    private String fileName;

    /** 访问地址（/uploads/...） */
    private String url;

    /** 文件大小（字节） */
    private Long size;

    /** 文件类型 */
    private String mimeType;
}
