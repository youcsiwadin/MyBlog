package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.PageRequest;
import com.chiaya.pojo.Page;

/**
 * 页面业务接口。
 */
public interface PageService {

    /**
     * 分页查询页面列表。
     */
    PageResult<Page> page(Integer page, Integer pageSize, String keyword, Integer status);

    /**
     * 查询页面详情。
     */
    Page getById(Long id);

    /**
     * 新建页面。
     */
    Page create(PageRequest request);

    /**
     * 编辑页面。
     */
    Page update(Long id, PageRequest request);

    /**
     * 逻辑删除页面。
     */
    void delete(Long id);
}
