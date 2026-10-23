package com.chiaya.common;

/**
 * 附加内容查看权限等级。
 *
 * <p>取值与用户角色（{@link UserRole}）保持一致，数值越小权限越高：
 * 用户角色小于等于该等级时即可查看附加内容。</p>
 */
public final class ExtraViewLevel {

    /** 仅管理员可查看 */
    public static final int ADMIN = UserRole.ADMIN;

    /** 作者及以上可查看 */
    public static final int AUTHOR = UserRole.AUTHOR;

    /** 订阅者及以上可查看（默认） */
    public static final int SUBSCRIBER = UserRole.SUBSCRIBER;

    /** 默认等级：订阅者及以上 */
    public static final int DEFAULT_LEVEL = SUBSCRIBER;

    private ExtraViewLevel() {
    }

    /**
     * 是否为合法的查看权限等级。
     */
    public static boolean isValid(Integer level) {
        return level != null && level >= ADMIN && level <= SUBSCRIBER;
    }

    /**
     * 等级名称。
     */
    public static String label(Integer level) {
        if (level == null) {
            return "未知";
        }
        return switch (level) {
            case ADMIN -> "仅管理员可查看";
            case AUTHOR -> "作者及以上可查看";
            case SUBSCRIBER -> "订阅者及以上可查看";
            default -> "未知";
        };
    }
}
