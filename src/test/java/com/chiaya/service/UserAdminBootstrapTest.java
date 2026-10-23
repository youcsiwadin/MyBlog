package com.chiaya.service;

import com.chiaya.common.BusinessException;
import com.chiaya.common.UserRole;
import com.chiaya.dao.UserDao;
import com.chiaya.dto.AdminRegisterRequest;
import com.chiaya.pojo.User;
import com.chiaya.service.impl.UserServiceImpl;
import com.chiaya.util.JwtUtil;
import com.chiaya.vo.LoginVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminBootstrapTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PermissionService permissionService;

    @Mock
    private SiteSettingService siteSettingService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("没有管理员时可注册首个管理员，并返回登录令牌")
    void shouldRegisterFirstAdmin() {
        when(userDao.countActiveAdmins()).thenReturn(0L);
        when(userDao.findByUsername("admin_bootstrap")).thenReturn(null);
        when(userDao.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(20L);
            return 1;
        });
        when(jwtUtil.createToken(20L)).thenReturn("admin-token");

        AdminRegisterRequest request = request();
        LoginVO result = userService.registerAdmin(request);

        assertEquals("admin-token", result.getToken());
        assertEquals(20L, result.getUser().getId());
        assertEquals(UserRole.ADMIN, result.getUser().getRole());
        assertEquals("admin@example.com", result.getUser().getEmail());
    }

    @Test
    @DisplayName("已有管理员时拒绝再次注册管理员")
    void shouldRejectWhenAdminExists() {
        when(userDao.countActiveAdmins()).thenReturn(1L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.registerAdmin(request()));

        assertEquals("管理员用户已存在，无法重复注册", exception.getMessage());
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("管理员注册邮箱必填")
    void shouldRequireAdminEmail() {
        when(userDao.countActiveAdmins()).thenReturn(0L);
        AdminRegisterRequest request = request();
        request.setEmail("  ");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.registerAdmin(request));

        assertEquals("邮箱不能为空", exception.getMessage());
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("管理员存在检测只统计启用管理员")
    void shouldDetectActiveAdmin() {
        when(userDao.countActiveAdmins()).thenReturn(1L);
        assertTrue(userService.adminExists());

        when(userDao.countActiveAdmins()).thenReturn(0L);
        assertFalse(userService.adminExists());
    }

    private AdminRegisterRequest request() {
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setUsername("admin_bootstrap");
        request.setPassword("123456");
        request.setNickname("首位管理员");
        request.setEmail(" ADMIN@EXAMPLE.COM ");
        return request;
    }
}
