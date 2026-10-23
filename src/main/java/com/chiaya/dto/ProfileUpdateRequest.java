package com.chiaya.dto;

import lombok.Data;

/**
 * 个人资料修改请求体。
 */
@Data
public class ProfileUpdateRequest {

    /** 昵称，必填 */
    private String nickname;

    /** 邮箱，可为空 */
    private String email;

    /** 头像地址，可为空 */
    private String avatar;
}
