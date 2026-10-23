package com.chiaya.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeMessage {
    private Long articleId;
    private Long articleAuthorId;
    private Long likeUserId;
    private String likeUserName;
    private String articleTitle;
    private Long timestamp;
}
