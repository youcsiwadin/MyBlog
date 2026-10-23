package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.GuestMessageRequest;
import com.chiaya.pojo.GuestMessage;

/**
 * 留言板业务接口。
 */
public interface GuestMessageService {

    /**
     * 分页查询留言（仅返回已通过的留言）。
     */
    PageResult<GuestMessage> page(Integer page, Integer pageSize);

    /**
     * 提交留言。
     */
    Long create(GuestMessageRequest request);
}
