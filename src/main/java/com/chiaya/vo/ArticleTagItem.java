package com.chiaya.vo;

import lombok.Data;

/**
 * 文章与标签的关联行，用于批量查询后按文章分组。
 */
@Data
public class ArticleTagItem {

    private Long articleId;

    private Long tagId;

    private String tagName;
}
