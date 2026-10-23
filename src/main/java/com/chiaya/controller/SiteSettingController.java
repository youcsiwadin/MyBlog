package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.dto.SiteSettingRequest;
import com.chiaya.service.SiteSettingService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.SiteSettingVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站点主题设置接口。
 */
@RestController
@RequestMapping("/api/settings")
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    private final PermissionService permissionService;

    public SiteSettingController(SiteSettingService siteSettingService, PermissionService permissionService) {
        this.siteSettingService = siteSettingService;
        this.permissionService = permissionService;
    }

    /** 查询站点主题设置 GET /api/settings */
    @GetMapping
    public Result<SiteSettingVO> settings() {
        return Result.success(siteSettingService.getSettings());
    }

    /** 保存站点主题设置 PUT /api/settings */
    @PutMapping
    public Result<SiteSettingVO> save(@RequestBody SiteSettingRequest request) {
        permissionService.requireAdmin();
        return Result.success("站点主题设置已保存", siteSettingService.saveSettings(request));
    }
}
