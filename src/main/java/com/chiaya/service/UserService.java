package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.LoginRequest;
import com.chiaya.dto.AdminRegisterRequest;
import com.chiaya.dto.PasswordChangeRequest;
import com.chiaya.dto.ProfileUpdateRequest;
import com.chiaya.dto.RegisterRequest;
import com.chiaya.dto.BloggerInfoRequest;
import com.chiaya.dto.UserCreateRequest;
import com.chiaya.vo.LoginVO;
import com.chiaya.vo.UserVO;

/**
 * 用户业务接口。
 */
public interface UserService {

    /**
     * 注册新用户。
     */
    UserVO register(RegisterRequest request);

    /**
     * 当前是否存在启用中的管理员。
     */
    boolean adminExists();

    /**
     * 仅在系统没有管理员时注册首个管理员。
     */
    LoginVO registerAdmin(AdminRegisterRequest request);

    /**
     * 用户登录，返回 Token 与用户信息。
     */
    LoginVO login(LoginRequest request);

    /**
     * 查询当前登录用户信息。
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 后台分页查询用户列表。
     */
    PageResult<UserVO> page(Integer page, Integer pageSize, String keyword);

    /**
     * 后台添加用户。
     */
    UserVO createUser(UserCreateRequest request);

    /**
     * 启用 / 禁用用户。
     */
    void updateStatus(Long id, Integer status);

    /**
     * 修改用户角色（仅管理员）。
     */
    void updateRole(Long id, Integer role);

    /**
     * 删除用户账号（仅管理员，逻辑删除）。
     */
    void deleteUser(Long id);

    /**
     * 修改个人资料。
     */
    UserVO updateProfile(Long userId, ProfileUpdateRequest request);

    /**
     * 修改密码。
     */
    void changePassword(Long userId, PasswordChangeRequest request);

    /**
     * 保存博主信息（博主名字 / 头像），管理员与作者可用。
     */
    UserVO updateBlogger(Long userId, BloggerInfoRequest request);

    /**
     * 判断用户是否具备管理员权限（super 超级管理员或 admin 管理员）。
     */
    boolean isAdmin(Long userId);
}
