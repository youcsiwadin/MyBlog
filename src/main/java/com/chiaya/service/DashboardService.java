package com.chiaya.service;

import com.chiaya.vo.DashboardSummaryVO;

/**
 * 首页仪表盘业务接口。
 */
public interface DashboardService {

    /**
     * 汇总首页统计数据。
     */
    DashboardSummaryVO summary();
}
