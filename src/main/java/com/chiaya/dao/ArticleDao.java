package com.chiaya.dao;

import com.chiaya.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章数据访问层，SQL 定义在 resources/mapper/ArticleMapper.xml。
 */
@Mapper
public interface ArticleDao {

    /**
     * 分页查询文章列表（不含正文）。
     */
    List<Article> pageList(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("tagId") Long tagId,
                           @Param("status") Integer status,
                           @Param("authorId") Long authorId,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    /**
     * 统计符合条件的文章总数。
     */
    long countByKeyword(@Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("tagId") Long tagId,
                        @Param("status") Integer status,
                        @Param("authorId") Long authorId);

    /**
     * 查询文章和作者。
     */
    Article selectById(@Param("id") Long id);

    /**
     * 统计某个作者已发布的文章数量。
     */
    long countPublishedByAuthor(@Param("authorId") Long authorId);

    /**
     * 查询文章详情（含正文与作者昵称）。
     */
    Article findById(@Param("id") Long id);

    /**
     * 新增文章，回填自增主键。
     */
    int insert(Article article);

    /**
     * 更新文章，仅作者本人可更新。
     */
    int update(Article article);

    /**
     * 浏览量 +1。
     */
    int increaseViewCount(@Param("id") Long id);


    /**
     * 点赞量 +1。
     */
    int incrementLikeCount(@Param("id") Long id);
    /**
     * 点赞量 -1.
     */
    int decrementLikeCount(@Param("id") Long id);
    /**
     * 逻辑删除文章，仅作者本人可删除。
     */
    int softDelete(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 修改文章状态（管理员审核用）。
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计全部文章数量。
     */
    long countAll();

    /**
     * 查询最新文章列表（不含正文）。
     */
    List<Article> recentList(@Param("limit") int limit);

    /**
     * 统计某个分类下的文章数量（用于删除分类前的校验）。
     */
    long countByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 文章管理筛选标签数量统计。
     *
     * @param authorId 数据范围：为 null 时统计全部作者，否则仅统计该作者
     */
    com.chiaya.vo.ArticleStatsVO stats(@Param("authorId") Long authorId);
}
