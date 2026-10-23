package com.chiaya.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeResultVO {
    private Long articleId;
    private Integer likeCount;
    private Boolean liked;
}
