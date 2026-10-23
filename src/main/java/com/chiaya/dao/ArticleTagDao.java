package com.chiaya.dao;

import com.chiaya.vo.ArticleTagItem;
import com.chiaya.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联数据访问层，SQL 定义在 resources/mapper/ArticleTagMapper.xml。
 */
@Mapper
public interface ArticleTagDao {

    /**
     * 查询某篇文章的标签列表。
     */
    List<TagVO> findTagsByArticleId(@Param("articleId") Long articleId);

    /**
     * 批量查询多篇文章的标签，用于列表页一次性装配。
     */
    List<ArticleTagItem> findTagsByArticleIds(@Param("articleIds") List<Long> articleIds);

    /**
     * 批量写入文章标签关系。
     */
    int insertBatch(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    /**
     * 删除某篇文章的全部标签关系（用于编辑时重建）。
     */
    int deleteByArticleId(@Param("articleId") Long articleId);
}
