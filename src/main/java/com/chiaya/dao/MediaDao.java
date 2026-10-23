package com.chiaya.dao;

import com.chiaya.pojo.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 媒体库数据访问层，SQL 定义在 resources/mapper/MediaMapper.xml。
 */
@Mapper
public interface MediaDao {

    List<Media> pageList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    long countAll();

    Media findById(@Param("id") Long id);

    /**
     * 根据主键集合批量查询媒体库文件（用于校验附件是否有效）。
     */
    List<Media> findByIds(@Param("ids") List<Long> ids);

    int insert(Media media);

    int deleteById(@Param("id") Long id);
}
