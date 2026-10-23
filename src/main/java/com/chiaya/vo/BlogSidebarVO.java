package com.chiaya.vo;

import lombok.Data;

import java.util.List;

/**
 * 博客站点左侧栏数据：博主信息 + 统计 + 分类与标签。
 */
@Data
public class BlogSidebarVO {

    /** 博主名称 */
    private String authorName;

    /** 博主头像 */
    private String authorAvatar;

    /** 已发布文章数量 */
    private Long articleCount;

    /** 分类数量 */
    private Long categoryCount;

    /** 标签数量 */
    private Long tagCount;

    /** 分类列表 */
    private List<TagVO> categories;

    /** 标签列表 */
    private List<TagVO> tags;
}
