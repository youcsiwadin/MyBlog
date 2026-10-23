package com.chiaya.vo;

import lombok.Data;

@Data
public class ArticleLikeVO {
    private Long articleId;
    private Long authorId;
    private Integer likeCount;
    private Boolean liked;
}
