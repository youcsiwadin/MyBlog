package com.chiaya.dao;

import com.chiaya.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签数据访问层，SQL 定义在 resources/mapper/TagMapper.xml。
 */
@Mapper
public interface TagDao {

    /**
     * 查询全部标签，并统计每个标签被多少篇文章使用。
     */
    List<Tag> listAll();

    Tag findById(@Param("id") Long id);

    Tag findByName(@Param("name") String name);

    List<Tag> findByIds(@Param("ids") List<Long> ids);

    int insert(Tag tag);

    int update(Tag tag);

    int softDelete(@Param("id") Long id);

    long countAll();
}
