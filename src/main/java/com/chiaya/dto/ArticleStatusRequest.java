package com.chiaya.dto;

import lombok.Data;

/**
 * 文章状态修改请求体（管理员审核用）。
 */
@Data
public class ArticleStatusRequest {

    /** 文章状态：0草稿，1待审核，2已发布 */
    private Integer status;
}
