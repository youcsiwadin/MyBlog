package com.chiaya.dto;

import lombok.Data;

/**
 * 修改密码请求体。
 */
@Data
public class PasswordChangeRequest {

    /** 原密码 */
    private String oldPassword;

    /** 新密码，6-50 位 */
    private String newPassword;
}
