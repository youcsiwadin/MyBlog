package com.chiaya.dao;

import com.chiaya.pojo.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 页面数据访问层，SQL 定义在 resources/mapper/PageMapper.xml。
 */
@Mapper
public interface PageDao {

    List<Page> pageList(@Param("keyword") String keyword,
                        @Param("status") Integer status,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword, @Param("status") Integer status);

    Page findById(@Param("id") Long id);

    Page findBySlug(@Param("slug") String slug);

    int insert(Page page);

    int update(Page page);

    int softDelete(@Param("id") Long id);

    long countAll();
}
