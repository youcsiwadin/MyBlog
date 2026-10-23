package com.chiaya.service;

import com.chiaya.vo.ArticleLikeVO;

public interface ArticleLikeService {
    /**
     * 点赞文章（需登录，幂等）
     */
    ArticleLikeVO like(Long articleId, Long userId);

    /**
     * 取消点赞（需登录，幂等）
     */
    ArticleLikeVO unlike(Long articleId, Long userId);

    /**
     * 查询当前用户是否已点赞
     */
    boolean isLiked(Long articleId, Long userId);

    /**
     * 获取文章点赞数
     */
    Integer getLikeCount(Long articleId);
}

