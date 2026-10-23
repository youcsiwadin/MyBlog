package com.chiaya.dto;

import lombok.Data;

/**
 * 留言提交请求体。
 */
@Data
public class GuestMessageRequest {

    /** 留言人昵称，必填 */
    private String nickname;

    /** 留言人邮箱，可为空 */
    private String email;

    /** 留言内容，必填，最长 1000 字 */
    private String content;
}
