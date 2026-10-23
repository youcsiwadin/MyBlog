package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.CommentRequest;
import com.chiaya.pojo.Comment;
import com.chiaya.vo.CommentStatsVO;

/**
 * 评论业务接口。
 */
public interface CommentService {

    /**
     * 分页查询评论列表。
     *
     * @param deleted 0 表示正常评论，1 表示回收站
     * @param mine    是否只看当前登录用户自己发表的评论
     */
    PageResult<Comment> page(Integer page, Integer pageSize, Integer status, String keyword,
                            Integer deleted, Boolean mine);

    /**
     * 评论管理筛选标签的数量统计。
     */
    CommentStatsVO stats();

    /**
     * 分页查询某篇文章已通过审核的评论（博客文章详情页使用）。
     */
    PageResult<Comment> pageByArticle(Long articleId, Integer page, Integer pageSize);

    /**
     * 提交评论（必须登录；默认待审核，开启「自动过审」后直接通过）。
     */
    Long create(CommentRequest request);

    /**
     * 查询「评论自动过审」开关状态。
     */
    boolean isAutoApproveEnabled();

    /**
     * 开启 / 关闭「评论自动过审」，返回最新状态。
     */
    boolean updateAutoApprove(boolean enabled);

    /**
     * 审核评论：1通过，2驳回。
     */
    void updateStatus(Long id, Integer status);

    /**
     * 逻辑删除评论。
     */
    void delete(Long id);

    /**
     * 从回收站恢复评论（恢复到待审核状态）。
     */
    void restore(Long id);

    /**
     * 彻底删除回收站中的评论（物理删除，不留记录）。
     */
    void purge(Long id);

    /**
     * 清理回收站中超过保存期限的评论（物理删除），返回清理条数。
     */
    int purgeExpired();

    /**
     * 统计待审核评论数量（仅管理员可调用）。
     */
    long countPending();
}
