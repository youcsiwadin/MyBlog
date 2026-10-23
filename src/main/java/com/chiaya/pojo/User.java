package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应数据表 user。
 */
@Data
public class User {

    /** 用户主键ID */
    private Long id;

    /** 登录用户名 */
    private String username;

    /** 密码摘要 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 头像地址 */
    private String avatar;

    /** 状态：1正常，0禁用 */
    private Integer status;

    /** 角色：1管理员，2作者，3订阅者 */
    private Integer role;

    /** 博主名字（博客展示用的署名） */
    private String bloggerName;

    /** 博主头像（博客展示用） */
    private String bloggerAvatar;

    /** 逻辑删除标记 */
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
