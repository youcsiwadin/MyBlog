package com.chiaya.dto;

import lombok.Data;

/**
 * 用户登录请求体。
 */
@Data
public class LoginRequest {

    /** 登录用户名 */
    private String username;

    /** 登录密码 */
    private String password;
}
