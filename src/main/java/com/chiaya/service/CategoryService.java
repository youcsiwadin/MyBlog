package com.chiaya.service;

import com.chiaya.dto.CategoryRequest;
import com.chiaya.pojo.Category;

import java.util.List;

/**
 * 分类目录业务接口。
 */
public interface CategoryService {

    /**
     * 查询全部分类目录（含文章数量统计）。
     */
    List<Category> list();

    /**
     * 新建分类。
     */
    Category create(CategoryRequest request);

    /**
     * 编辑分类。
     */
    Category update(Long id, CategoryRequest request);

    /**
     * 逻辑删除分类（分类下存在文章时不允许删除）。
     */
    void delete(Long id);
}
