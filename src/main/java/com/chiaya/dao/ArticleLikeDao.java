package com.chiaya.dao;

import com.chiaya.pojo.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleLikeDao {
    int insert(ArticleLike articleLike);
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId,
                                   @Param("userId") Long userId);
    ArticleLike selectByArticleIdAndUserId(@Param("articleId") Long articleId,
                                           @Param("userId") Long userId);
    long countByArticleId(@Param("articleId") Long articleId);

}
