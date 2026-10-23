package com.chiaya.dto;

import lombok.Data;

/**
 * 用户状态变更请求体。
 */
@Data
public class UserStatusRequest {

    /** 目标状态：1正常，0禁用 */
    private Integer status;
}
