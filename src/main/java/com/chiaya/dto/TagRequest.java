package com.chiaya.dto;

import lombok.Data;

/**
 * 标签新建 / 编辑请求体。
 */
@Data
public class TagRequest {

    /** 标签名称，必填，最长 50 字 */
    private String name;
}
