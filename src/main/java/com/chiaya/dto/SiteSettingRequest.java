package com.chiaya.dto;

import lombok.Data;

/**
 * 站点主题设置保存请求体。
 */
@Data
public class SiteSettingRequest {

    private String siteName;

    private String siteDescription;

    private String footerText;

    private String primaryColor;

    private String themeStyle;

    private String logoUrl;

    /** 博客封面标题 */
    private String coverTitle;

    /** 博客封面副标题 */
    private String coverSubtitle;

    /** 博客封面图片地址 */
    private String coverImage;

    /** 夜间模式封面背景图片地址 */
    private String nightCoverImage;

    /** 夜间模式切换：time / night / day */
    private String nightModeSwitch;

    /** 公告 */
    private String notice;

    /** 公告栏（多行文本，每行一条公告） */
    private String noticeBoard;

    /** 博主名称 */
    private String authorName;

    /** 博主头像地址 */
    private String authorAvatar;
}
