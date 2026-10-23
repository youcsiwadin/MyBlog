package com.chiaya.dao;

import com.chiaya.vo.ExtraFileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章附加内容附件数据访问层，SQL 定义在 resources/mapper/ArticleExtraFileMapper.xml。
 */
@Mapper
public interface ArticleExtraFileDao {

    /**
     * 查询某篇文章的附加内容附件（联表媒体库）。
     */
    List<ExtraFileVO> findByArticleId(@Param("articleId") Long articleId);

    /**
     * 批量写入附件关系。
     */
    int insertBatch(@Param("articleId") Long articleId, @Param("mediaIds") List<Long> mediaIds);

    /**
     * 删除某篇文章的全部附件关系（编辑时重建）。
     */
    int deleteByArticleId(@Param("articleId") Long articleId);
}
