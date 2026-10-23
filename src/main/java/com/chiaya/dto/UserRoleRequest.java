package com.chiaya.dto;

import lombok.Data;

/**
 * 用户角色修改请求体。
 */
@Data
public class UserRoleRequest {

    /** 角色：1管理员，2作者，3订阅者 */
    private Integer role;
}
