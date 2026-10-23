package com.chiaya.vo;

import lombok.Data;

/**
 * 评论管理筛选标签的数量统计。
 */
@Data
public class CommentStatsVO {

    /** 全部（未删除） */
    private Long allCount;

    /** 我的（当前登录用户发表的、未删除的评论） */
    private Long mineCount;

    /** 待审（未删除且待审核） */
    private Long pendingCount;

    /** 已批准（未删除且已通过） */
    private Long approvedCount;

    /** 回收站（已逻辑删除） */
    private Long trashCount;
}
