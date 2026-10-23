package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类目录实体，对应数据表 category。
 */
@Data
public class Category {

    private Long id;

    private String name;

    private String slug;

    private String description;

    /** 排序值，越小越靠前 */
    private Integer sortOrder;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 该分类下的文章数量（统计字段，非数据库列） */
    private Integer articleCount;
}
