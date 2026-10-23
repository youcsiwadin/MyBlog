package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.common.PageResult;
import com.chiaya.context.UserContext;
import com.chiaya.dto.LoginRequest;
import com.chiaya.dto.AdminRegisterRequest;
import com.chiaya.dto.PasswordChangeRequest;
import com.chiaya.dto.ProfileUpdateRequest;
import com.chiaya.dto.RegisterRequest;
import com.chiaya.dto.UserCreateRequest;
import com.chiaya.dto.BloggerInfoRequest;
import com.chiaya.dto.UserRoleRequest;
import com.chiaya.dto.UserStatusRequest;
import com.chiaya.service.PermissionService;
import com.chiaya.service.UserService;
import com.chiaya.vo.LoginVO;
import com.chiaya.vo.UserVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口：注册、登录、当前用户、用户管理、个人资料。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final PermissionService permissionService;

    public UserController(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    /**
     * 用户注册。
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<Result<UserVO>> register(@RequestBody RegisterRequest request) {
        UserVO user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("注册成功", user));
    }

    /**
     * 检测系统是否存在启用中的管理员。
     * GET /api/users/admin-exists
     */
    @GetMapping("/admin-exists")
    public Result<Boolean> adminExists() {
        return Result.success(userService.adminExists());
    }

    /**
     * 首次管理员注册；系统中已有管理员时拒绝。
     * POST /api/users/register-admin
     */
    @PostMapping("/register-admin")
    public ResponseEntity<Result<LoginVO>> registerAdmin(@RequestBody AdminRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("管理员注册成功", userService.registerAdmin(request)));
    }

    /**
     * 用户登录。
     * POST /api/users/login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        return Result.success("登录成功", userService.login(request));
    }

    /**
     * 获取当前登录用户信息。
     * GET /api/users/me
     */
    @GetMapping("/me")
    public Result<UserVO> currentUser() {
        return Result.success(userService.getCurrentUser(UserContext.getUserId()));
    }

    /**
     * 分页查询用户列表（后台管理）。
     * GET /api/users?page=1&pageSize=10&keyword=
     */
    @GetMapping
    public Result<PageResult<UserVO>> page(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String keyword) {
        permissionService.requireAdmin();
        return Result.success(userService.page(page, pageSize, keyword));
    }

    /**
     * 后台添加用户。
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<Result<UserVO>> create(@RequestBody UserCreateRequest request) {
        permissionService.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("用户创建成功", userService.createUser(request)));
    }

    /**
     * 启用 / 禁用用户。
     * PATCH /api/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody UserStatusRequest request) {
        permissionService.requireAdmin();
        userService.updateStatus(id, request.getStatus());
        return Result.success("用户状态更新成功", null);
    }

    /**
     * 修改用户角色（仅管理员）。
     * PUT /api/users/{id}/role
     */
    @PutMapping("/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody UserRoleRequest request) {
        userService.updateRole(id, request.getRole());
        return Result.success("用户角色更新成功", null);
    }

    /**
     * 删除用户账号（仅管理员，逻辑删除）。
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户账号已删除", null);
    }

    /**
     * 修改个人资料。
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public Result<UserVO> updateProfile(@RequestBody ProfileUpdateRequest request) {
        permissionService.requireAdminSystem();
        return Result.success("个人资料已更新", userService.updateProfile(UserContext.getUserId(), request));
    }

    /**
     * 修改密码。
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public Result<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        permissionService.requireAdminSystem();
        userService.changePassword(UserContext.getUserId(), request);
        return Result.success("密码修改成功", null);
    }

    /**
     * 保存博主信息（博主名字 / 头像），管理员与作者可用。
     * PUT /api/users/me/blogger
     */
    @PutMapping("/me/blogger")
    public Result<UserVO> updateBlogger(@RequestBody BloggerInfoRequest request) {
        return Result.success("博主信息已保存", userService.updateBlogger(UserContext.getUserId(), request));
    }
}
