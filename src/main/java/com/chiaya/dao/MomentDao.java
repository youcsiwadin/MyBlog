package com.chiaya.dao;

import com.chiaya.pojo.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 说说数据访问层，SQL 定义在 resources/mapper/MomentMapper.xml。
 */
@Mapper
public interface MomentDao {

    List<Moment> pageList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    long countAll();

    Moment findById(@Param("id") Long id);

    int insert(Moment moment);

    int softDelete(@Param("id") Long id, @Param("userId") Long userId);
}
