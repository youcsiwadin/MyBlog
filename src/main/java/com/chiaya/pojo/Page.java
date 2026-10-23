package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 独立页面实体，对应数据表 page。
 */
@Data
public class Page {

    private Long id;

    private String title;

    private String slug;

    private String content;

    /** 状态：1已发布，0草稿 */
    private Integer status;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
