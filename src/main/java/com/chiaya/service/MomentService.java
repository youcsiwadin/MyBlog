package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.MomentRequest;
import com.chiaya.pojo.Moment;

/**
 * 说说业务接口。
 */
public interface MomentService {

    /**
     * 分页查询说说列表。
     */
    PageResult<Moment> page(Integer page, Integer pageSize);

    /**
     * 发表说说。
     */
    Long create(Long userId, MomentRequest request);

    /**
     * 删除自己的说说。
     */
    void delete(Long userId, Long id);
}
