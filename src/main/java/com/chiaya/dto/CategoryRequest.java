package com.chiaya.dto;

import lombok.Data;

/**
 * 分类目录新建 / 编辑请求体。
 */
@Data
public class CategoryRequest {

    /** 分类名称，必填，最长 50 字 */
    private String name;

    /** 分类别名，可为空，最长 100 字 */
    private String slug;

    /** 分类描述，可为空，最长 255 字 */
    private String description;

    /** 排序值，可为空，默认 0 */
    private Integer sortOrder;
}
