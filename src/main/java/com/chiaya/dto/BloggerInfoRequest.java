package com.chiaya.dto;

import lombok.Data;

/**
 * 博主信息（博主名字 / 头像）保存请求体。
 */
@Data
public class BloggerInfoRequest {

    /** 博主名字，最长 50 字 */
    private String bloggerName;

    /** 博主头像地址，最长 500 字 */
    private String bloggerAvatar;
}
