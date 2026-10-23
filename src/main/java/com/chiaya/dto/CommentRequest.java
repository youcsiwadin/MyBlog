package com.chiaya.dto;

import lombok.Data;

/**
 * 评论提交请求体（登录用户提交，默认待审核）。
 */
@Data
public class CommentRequest {

    /** 所属文章ID，必填 */
    private Long articleId;

    /** 兼容字段：评论人昵称由后端按登录账号生成 */
    private String nickname;

    /** 评论人邮箱，可为空 */
    private String email;

    /** 评论内容，必填，最长 1000 字 */
    private String content;
}
