package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.service.DashboardService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.DashboardSummaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页仪表盘接口。
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    private final PermissionService permissionService;

    public DashboardController(DashboardService dashboardService, PermissionService permissionService) {
        this.dashboardService = dashboardService;
        this.permissionService = permissionService;
    }

    /** 首页统计数据 GET /api/dashboard/summary */
    @GetMapping("/summary")
    public Result<DashboardSummaryVO> summary() {
        permissionService.requireAdmin();
        return Result.success(dashboardService.summary());
    }
}
