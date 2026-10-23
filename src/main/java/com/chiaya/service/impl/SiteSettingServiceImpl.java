package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.dao.SiteSettingDao;
import com.chiaya.dto.SiteSettingRequest;
import com.chiaya.pojo.SiteSetting;
import com.chiaya.service.SiteSettingService;
import com.chiaya.vo.SiteSettingVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 站点主题设置业务实现。
 */
@Service
public class SiteSettingServiceImpl implements SiteSettingService {

    private static final String KEY_SITE_NAME = "site_name";

    private static final String KEY_SITE_DESCRIPTION = "site_description";

    private static final String KEY_FOOTER_TEXT = "footer_text";

    private static final String KEY_PRIMARY_COLOR = "primary_color";

    private static final String KEY_THEME_STYLE = "theme_style";

    private static final String KEY_LOGO_URL = "logo_url";

    private static final String KEY_COVER_TITLE = "cover_title";

    private static final String KEY_COVER_SUBTITLE = "cover_subtitle";

    private static final String KEY_COVER_IMAGE = "cover_image";

    private static final String KEY_NIGHT_COVER_IMAGE = "night_cover_image";

    private static final String KEY_NIGHT_MODE_SWITCH = "night_mode_switch";

    private static final String KEY_NOTICE = "notice";

    private static final String KEY_NOTICE_BOARD = "notice_board";

    private static final String KEY_AUTHOR_NAME = "author_name";

    private static final String KEY_AUTHOR_AVATAR = "author_avatar";

    private static final Pattern COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private static final int TEXT_MAX_LENGTH = 200;

    private static final int URL_MAX_LENGTH = 500;

    private static final int COVER_TITLE_MAX_LENGTH = 50;

    private static final int COVER_SUBTITLE_MAX_LENGTH = 100;

    private static final int NOTICE_MAX_LENGTH = 500;

    private static final int NOTICE_BOARD_MAX_LENGTH = 1000;

    private final SiteSettingDao siteSettingDao;

    public SiteSettingServiceImpl(SiteSettingDao siteSettingDao) {
        this.siteSettingDao = siteSettingDao;
    }

    @Override
    public SiteSettingVO getSettings() {
        SiteSettingVO vo = new SiteSettingVO();
        vo.setSiteName("JAVA博客网站");
        vo.setSiteDescription("");
        vo.setFooterText("");
        vo.setPrimaryColor("#409eff");
        vo.setThemeStyle("light");
        vo.setLogoUrl("");
        vo.setCoverTitle("博客");
        vo.setCoverSubtitle("我的博客");
        vo.setCoverImage("");
        vo.setNightCoverImage("");
        vo.setNightModeSwitch("day");
        vo.setNotice("");
        vo.setNoticeBoard("");
        vo.setAuthorName("御园千绫");
        vo.setAuthorAvatar("");

        List<SiteSetting> settings = siteSettingDao.listAll();
        for (SiteSetting setting : settings) {
            String value = setting.getSettingValue();
            switch (setting.getSettingKey()) {
                case KEY_SITE_NAME -> vo.setSiteName(value);
                case KEY_SITE_DESCRIPTION -> vo.setSiteDescription(value);
                case KEY_FOOTER_TEXT -> vo.setFooterText(value);
                case KEY_PRIMARY_COLOR -> vo.setPrimaryColor(value);
                case KEY_THEME_STYLE -> vo.setThemeStyle(value);
                case KEY_LOGO_URL -> vo.setLogoUrl(value);
                case KEY_COVER_TITLE -> vo.setCoverTitle(value);
                case KEY_COVER_SUBTITLE -> vo.setCoverSubtitle(value);
                case KEY_COVER_IMAGE -> vo.setCoverImage(value);
                case KEY_NIGHT_COVER_IMAGE -> vo.setNightCoverImage(value);
                case KEY_NIGHT_MODE_SWITCH -> vo.setNightModeSwitch(value);
                case KEY_NOTICE -> vo.setNotice(value);
                case KEY_NOTICE_BOARD -> vo.setNoticeBoard(value);
                case KEY_AUTHOR_NAME -> vo.setAuthorName(value);
                case KEY_AUTHOR_AVATAR -> vo.setAuthorAvatar(value);
                default -> {
                    // 未知配置项忽略
                }
            }
        }
        return vo;
    }

    @Override
    public SiteSettingVO saveSettings(SiteSettingRequest request) {
        String siteName = requireText(request.getSiteName(), "站点名称", 50);
        String siteDescription = optionalText(request.getSiteDescription(), "站点副标题", TEXT_MAX_LENGTH);
        String footerText = optionalText(request.getFooterText(), "页脚文字", TEXT_MAX_LENGTH);
        String primaryColor = normalizeColor(request.getPrimaryColor());
        String themeStyle = normalizeThemeStyle(request.getThemeStyle());
        String logoUrl = optionalText(request.getLogoUrl(), "Logo 地址", URL_MAX_LENGTH);
        String coverTitle = defaultText(request.getCoverTitle(), "博客", "封面标题", COVER_TITLE_MAX_LENGTH);
        String coverSubtitle = defaultText(request.getCoverSubtitle(), "我的博客", "封面副标题",
                COVER_SUBTITLE_MAX_LENGTH);
        String coverImage = optionalText(request.getCoverImage(), "封面图片地址", URL_MAX_LENGTH);
        String nightCoverImage = optionalText(request.getNightCoverImage(), "夜间封面图片地址", URL_MAX_LENGTH);
        String nightModeSwitch = normalizeNightModeSwitch(request.getNightModeSwitch());
        String notice = optionalText(request.getNotice(), "公告", NOTICE_MAX_LENGTH);
        String noticeBoard = optionalText(request.getNoticeBoard(), "公告栏", NOTICE_BOARD_MAX_LENGTH);
        String authorName = defaultText(request.getAuthorName(), "御园千绫", "博主名称", COVER_TITLE_MAX_LENGTH);
        String authorAvatar = optionalText(request.getAuthorAvatar(), "博主头像地址", URL_MAX_LENGTH);

        siteSettingDao.upsert(KEY_SITE_NAME, siteName);
        siteSettingDao.upsert(KEY_SITE_DESCRIPTION, siteDescription);
        siteSettingDao.upsert(KEY_FOOTER_TEXT, footerText);
        siteSettingDao.upsert(KEY_PRIMARY_COLOR, primaryColor);
        siteSettingDao.upsert(KEY_THEME_STYLE, themeStyle);
        siteSettingDao.upsert(KEY_LOGO_URL, logoUrl);
        siteSettingDao.upsert(KEY_COVER_TITLE, coverTitle);
        siteSettingDao.upsert(KEY_COVER_SUBTITLE, coverSubtitle);
        siteSettingDao.upsert(KEY_COVER_IMAGE, coverImage);
        siteSettingDao.upsert(KEY_NIGHT_COVER_IMAGE, nightCoverImage);
        siteSettingDao.upsert(KEY_NIGHT_MODE_SWITCH, nightModeSwitch);
        siteSettingDao.upsert(KEY_NOTICE, notice);
        siteSettingDao.upsert(KEY_NOTICE_BOARD, noticeBoard);
        siteSettingDao.upsert(KEY_AUTHOR_NAME, authorName);
        siteSettingDao.upsert(KEY_AUTHOR_AVATAR, authorAvatar);

        return getSettings();
    }

    @Override
    public void updateAuthorInfo(String authorName, String authorAvatar) {
        String name = defaultText(authorName, "御园千绫", "博主名称", COVER_TITLE_MAX_LENGTH);
        String avatar = optionalText(authorAvatar, "博主头像地址", URL_MAX_LENGTH);
        siteSettingDao.upsert(KEY_AUTHOR_NAME, name);
        siteSettingDao.upsert(KEY_AUTHOR_AVATAR, avatar);
    }

    private String requireText(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest(fieldName + "不能为空");
        }
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    private String optionalText(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    /**
     * 允许为空的文本项：为空时使用默认值。
     */
    private String defaultText(String value, String defaultValue, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            return defaultValue;
        }
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    private String normalizeColor(String color) {
        String trimmed = color == null ? "" : color.trim();
        if (trimmed.isEmpty()) {
            return "#409eff";
        }
        if (!COLOR_PATTERN.matcher(trimmed).matches()) {
            throw BusinessException.badRequest("主色调格式不正确，应为 #RRGGBB");
        }
        return trimmed.toLowerCase();
    }

    private String normalizeThemeStyle(String themeStyle) {
        String trimmed = themeStyle == null ? "" : themeStyle.trim();
        if (trimmed.isEmpty()) {
            return "light";
        }
        if (!"light".equals(trimmed) && !"dark".equals(trimmed)) {
            throw BusinessException.badRequest("主题风格只能是 light 或 dark");
        }
        return trimmed;
    }

    /**
     * 夜间模式切换规则：time 根据时间（22:00~7:00）/ night 保持夜间 / day 保持白天。
     */
    private String normalizeNightModeSwitch(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            return "day";
        }
        if (!"time".equals(trimmed) && !"night".equals(trimmed) && !"day".equals(trimmed)) {
            throw BusinessException.badRequest("夜间模式切换规则不合法");
        }
        return trimmed;
    }
}
