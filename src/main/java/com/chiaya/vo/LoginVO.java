package com.chiaya.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功响应对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** 访问令牌，前端保存后放入 Authorization 请求头 */
    private String token;

    /** 登录用户信息 */
    private UserVO user;
}
