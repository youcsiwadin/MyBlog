package com.chiaya.service;

import com.chiaya.dto.SiteSettingRequest;
import com.chiaya.vo.SiteSettingVO;

/**
 * 站点主题设置业务接口。
 */
public interface SiteSettingService {

    /**
     * 查询站点主题设置。
     */
    SiteSettingVO getSettings();

    /**
     * 保存站点主题设置。
     */
    SiteSettingVO saveSettings(SiteSettingRequest request);

    /**
     * 仅更新站点级博主信息（博主名字 / 头像）。
     */
    void updateAuthorInfo(String authorName, String authorAvatar);
}
