package com.chiaya.dao;

import com.chiaya.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类目录数据访问层，SQL 定义在 resources/mapper/CategoryMapper.xml。
 */
@Mapper
public interface CategoryDao {

    /**
     * 查询全部分类（按排序值、创建时间排序），并统计每个分类下的文章数。
     */
    List<Category> listAll();

    Category findById(@Param("id") Long id);

    Category findByName(@Param("name") String name);

    int insert(Category category);

    int update(Category category);

    int softDelete(@Param("id") Long id);

    long countAll();
}
