package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体，对应数据表 tag。
 */
@Data
public class Tag {

    private Long id;

    private String name;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 使用该标签的文章数量（统计字段，非数据库列） */
    private Integer articleCount;
}
