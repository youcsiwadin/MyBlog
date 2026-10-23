package com.chiaya.vo;

import lombok.Data;

/**
 * 站点主题设置响应对象。
 */
@Data
public class SiteSettingVO {

    /** 站点名称 */
    private String siteName;

    /** 站点副标题 */
    private String siteDescription;

    /** 页脚文字 */
    private String footerText;

    /** 主题主色调 */
    private String primaryColor;

    /** 主题风格：light / dark */
    private String themeStyle;

    /** 站点 Logo 地址 */
    private String logoUrl;

    /** 博客封面标题 */
    private String coverTitle;

    /** 博客封面副标题 */
    private String coverSubtitle;

    /** 博客封面图片地址 */
    private String coverImage;

    /** 夜间模式封面背景图片地址 */
    private String nightCoverImage;

    /** 夜间模式切换：time 根据时间(22:00~7:00)，night 保持夜间，day 保持白天 */
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
