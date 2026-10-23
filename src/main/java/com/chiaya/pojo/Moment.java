package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 说说实体，对应数据表 moment。
 */
@Data
public class Moment {

    private Long id;

    private Long userId;

    private String content;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 发布人昵称（联表查询字段，非数据库列） */
    private String authorName;
}
