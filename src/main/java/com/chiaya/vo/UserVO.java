package com.chiaya.vo;

import com.chiaya.pojo.User;
import lombok.Data;

/**
 * 用户信息响应对象（不包含密码）。
 */
@Data
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 登录用户名 */
    private String username;

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

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setRole(user.getRole());
        vo.setBloggerName(user.getBloggerName());
        vo.setBloggerAvatar(user.getBloggerAvatar());
        return vo;
    }
}
