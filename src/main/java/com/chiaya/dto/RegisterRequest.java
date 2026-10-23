package com.chiaya.dto;

import lombok.Data;

/**
 * 用户注册请求体。
 */
@Data
public class RegisterRequest {

    /** 登录用户名，4-50 位 */
    private String username;

    /** 登录密码，6-50 位 */
    private String password;

    /** 昵称，可为空，默认取用户名 */
    private String nickname;

    /** 邮箱，可为空 */
    private String email;
}
