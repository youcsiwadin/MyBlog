package com.chiaya.service;

import com.chiaya.dto.TagRequest;
import com.chiaya.pojo.Tag;

import java.util.List;

/**
 * 标签业务接口。
 */
public interface TagService {

    /**
     * 查询全部标签（含使用数量统计）。
     */
    List<Tag> list();

    /**
     * 新建标签。
     */
    Tag create(TagRequest request);

    /**
     * 编辑标签。
     */
    Tag update(Long id, TagRequest request);

    /**
     * 逻辑删除标签。
     */
    void delete(Long id);
}
