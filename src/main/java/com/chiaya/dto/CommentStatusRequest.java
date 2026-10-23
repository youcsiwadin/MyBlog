package com.chiaya.dto;

import lombok.Data;

/**
 * 评论审核请求体。
 */
@Data
public class CommentStatusRequest {

    /** 目标状态：1通过，2驳回 */
    private Integer status;
}
