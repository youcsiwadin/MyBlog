package com.chiaya.dto;

import lombok.Data;

/**
 * 说说发布请求体。
 */
@Data
public class MomentRequest {

    /** 说说内容，必填，最长 1000 字 */
    private String content;
}
