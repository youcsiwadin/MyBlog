package com.chiaya.vo;

import lombok.Data;

/**
 * 文章管理筛选标签的数量统计。
 */
@Data
public class ArticleStatsVO {

    /** 全部（未删除） */
    private Long allCount;

    /** 已发布 */
    private Long publishedCount;

    /** 待审核 */
    private Long pendingCount;

    /** 草稿 */
    private Long draftCount;
}
