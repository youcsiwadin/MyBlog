package com.chiaya.vo;

import com.chiaya.pojo.Article;
import com.chiaya.pojo.Comment;
import lombok.Data;

import java.util.List;

/**
 * 首页仪表盘统计数据。
 */
@Data
public class DashboardSummaryVO {

    private Long articleCount;

    private Long categoryCount;

    private Long tagCount;

    private Long pageCount;

    private Long commentCount;

    private Long pendingCommentCount;

    private Long momentCount;

    private Long userCount;

    /** 最新文章 */
    private List<Article> recentArticles;

    /** 最新评论 */
    private List<Comment> recentComments;
}
