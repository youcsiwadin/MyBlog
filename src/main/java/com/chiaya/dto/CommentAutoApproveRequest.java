package com.chiaya.dto;

import lombok.Data;

/**
 * 评论自动过审开关请求体。
 */
@Data
public class CommentAutoApproveRequest {

    /** 是否开启自动过审：true 开启，false 关闭 */
    private Boolean enabled;
}
