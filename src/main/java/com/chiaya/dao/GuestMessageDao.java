package com.chiaya.dao;

import com.chiaya.pojo.GuestMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言板数据访问层，SQL 定义在 resources/mapper/GuestMessageMapper.xml。
 */
@Mapper
public interface GuestMessageDao {

    List<GuestMessage> pageList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    long countAll();

    int insert(GuestMessage message);
}
