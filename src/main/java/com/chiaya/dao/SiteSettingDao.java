package com.chiaya.dao;

import com.chiaya.pojo.SiteSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站点设置数据访问层，SQL 定义在 resources/mapper/SiteSettingMapper.xml。
 */
@Mapper
public interface SiteSettingDao {

    List<SiteSetting> listAll();

    /**
     * 按配置项键名查询配置值，不存在时返回 null。
     */
    String findValueByKey(@Param("key") String key);

    /**
     * 写入或更新配置项（存在则更新）。
     */
    int upsert(@Param("settingKey") String settingKey, @Param("settingValue") String settingValue);
}
