package com.chiaya.vo;

import lombok.Data;

/**
 * 博客作者信息（文章作者 / 博主）。
 */
@Data
public class BlogAuthorVO {

    /** 作者用户ID */
    private Long id;

    /** 作者名称（昵称，昵称为空时用用户名） */
    private String name;

    /** 作者头像 */
    private String avatar;

    /** 已发布文章数量 */
    private Long articleCount;
}
