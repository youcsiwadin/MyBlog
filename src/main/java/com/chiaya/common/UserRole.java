package com.chiaya.common;

/**
 * 用户角色定义。
 */
public final class UserRole {

    /** 管理员：最高权限，可使用所有功能 */
    public static final int ADMIN = 1;

    /** 作者：文章（所有文章 / 写文章）、我的评论、个人资料 */
    public static final int AUTHOR = 2;

    /** 订阅者：我的评论、个人资料（最低权限） */
    public static final int SUBSCRIBER = 3;

    private UserRole() {
    }

    /**
     * 是否为合法的角色值。
     */
    public static boolean isValid(Integer role) {
        return role != null && role >= ADMIN && role <= SUBSCRIBER;
    }

    /**
     * 角色名称。
     */
    public static String label(Integer role) {
        if (role == null) {
            return "未知";
        }
        return switch (role) {
            case ADMIN -> "管理员";
            case AUTHOR -> "作者";
            case SUBSCRIBER -> "订阅者";
            default -> "未知";
        };
    }
}
