package com.chiaya.dao;

import com.chiaya.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论数据访问层，SQL 定义在 resources/mapper/CommentMapper.xml。
 */
@Mapper
public interface CommentDao {

    List<Comment> pageList(@Param("status") Integer status,
                           @Param("keyword") String keyword,
                           @Param("deleted") Integer deleted,
                           @Param("userId") Long userId,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long countByCondition(@Param("status") Integer status,
                          @Param("keyword") String keyword,
                          @Param("deleted") Integer deleted,
                          @Param("userId") Long userId);

    /**
     * 统计评论的全局 / 我的 / 待审 / 已批准 / 回收站数量。
     */
    com.chiaya.vo.CommentStatsVO stats(@Param("userId") Long userId);

    /**
     * 查询某篇文章已通过审核的评论（博客展示用）。
     */
    List<Comment> pageListByArticle(@Param("articleId") Long articleId,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    /**
     * 统计某篇文章已通过审核的评论数量。
     */
    long countByArticle(@Param("articleId") Long articleId);

    Comment findById(@Param("id") Long id);

    int insert(Comment comment);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int softDelete(@Param("id") Long id);

    /**
     * 文章被删除时，把该文章下所有正常评论一并移入回收站。
     *
     * @return 受影响（被移入回收站）的评论条数
     */
    int softDeleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 从回收站恢复评论（恢复到待审核状态）。
     */
    int restore(@Param("id") Long id);

    /**
     * 彻底删除回收站中的评论（物理删除，不留记录）。
     */
    int purge(@Param("id") Long id);

    /**
     * 清理回收站中超过保存期限的评论（物理删除）。
     */
    int purgeExpired();

    long countAll();

    long countByStatus(@Param("status") Integer status);

    List<Comment> recentList(@Param("limit") int limit);
}
