package com.chiaya.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 权限分级回归测试：只允许管理员、作者、订阅者三个等级。
 */
class RoleLevelTest {

    @Test
    @DisplayName("用户角色只允许 1/2/3，游客角色 4 无效")
    void userRoleShouldOnlyAllowThreeLevels() {
        assertTrue(UserRole.isValid(UserRole.ADMIN));
        assertTrue(UserRole.isValid(UserRole.AUTHOR));
        assertTrue(UserRole.isValid(UserRole.SUBSCRIBER));
        assertFalse(UserRole.isValid(4));
    }

    @Test
    @DisplayName("附加内容分级只允许 1/2/3，游客分级 4 无效")
    void extraViewLevelShouldOnlyAllowThreeLevels() {
        assertTrue(ExtraViewLevel.isValid(ExtraViewLevel.ADMIN));
        assertTrue(ExtraViewLevel.isValid(ExtraViewLevel.AUTHOR));
        assertTrue(ExtraViewLevel.isValid(ExtraViewLevel.SUBSCRIBER));
        assertFalse(ExtraViewLevel.isValid(4));
    }
}
