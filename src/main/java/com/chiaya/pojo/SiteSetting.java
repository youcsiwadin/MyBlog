package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站点设置实体，对应数据表 site_setting（键值结构）。
 */
@Data
public class SiteSetting {

    private Long id;

    private String settingKey;

    private String settingValue;

    private LocalDateTime updateTime;
}
