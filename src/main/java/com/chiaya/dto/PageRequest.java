package com.chiaya.dto;

import lombok.Data;

/**
 * 页面新建 / 编辑请求体。
 */
@Data
public class PageRequest {

    /** 页面标题，必填，最长 200 字 */
    private String title;

    /** 页面别名，可为空，最长 100 字，需唯一 */
    private String slug;

    /** 页面内容，必填 */
    private String content;

    /** 状态：1已发布，0草稿，默认 1 */
    private Integer status;
}
