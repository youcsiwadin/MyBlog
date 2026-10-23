package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.common.UserRole;
import com.chiaya.context.UserContext;
import com.chiaya.dao.UserDao;
import com.chiaya.dto.AdminRegisterRequest;
import com.chiaya.dto.LoginRequest;
import com.chiaya.dto.PasswordChangeRequest;
import com.chiaya.dto.ProfileUpdateRequest;
import com.chiaya.dto.RegisterRequest;
import com.chiaya.dto.UserCreateRequest;
import com.chiaya.dto.BloggerInfoRequest;
import com.chiaya.service.SiteSettingService;
import com.chiaya.pojo.User;
import com.chiaya.service.UserService;
import com.chiaya.service.PermissionService;
import com.chiaya.util.JwtUtil;
import com.chiaya.util.PasswordUtil;
import com.chiaya.vo.LoginVO;
import com.chiaya.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * 用户业务实现。
 */
@Service
public class UserServiceImpl implements UserService {

    private static final int USERNAME_MIN_LENGTH = 4;

    private static final int USERNAME_MAX_LENGTH = 50;

    private static final int PASSWORD_MIN_LENGTH = 6;

    private static final int PASSWORD_MAX_LENGTH = 50;

    private static final int NICKNAME_MAX_LENGTH = 50;

    private static final int EMAIL_MAX_LENGTH = 100;

    private static final int AVATAR_MAX_LENGTH = 255;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** 单实例部署下防止并发创建多个首个管理员。 */
    private static final ReentrantLock ADMIN_BOOTSTRAP_LOCK = new ReentrantLock(true);

    private final UserDao userDao;

    private final JwtUtil jwtUtil;

    private final PermissionService permissionService;

    private final SiteSettingService siteSettingService;

    public UserServiceImpl(UserDao userDao, JwtUtil jwtUtil, PermissionService permissionService,
                           SiteSettingService siteSettingService) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.permissionService = permissionService;
        this.siteSettingService = siteSettingService;
    }

    @Override
    public UserVO register(RegisterRequest request) {
        String username = trim(request.getUsername());
        String password = request.getPassword();
        if (username.isEmpty()) {
            throw BusinessException.badRequest("用户名不能为空");
        }
        if (username.length() < USERNAME_MIN_LENGTH || username.length() > USERNAME_MAX_LENGTH) {
            throw BusinessException.badRequest("用户名长度必须为 4-50 个字符");
        }
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw BusinessException.badRequest("密码长度必须为 6-50 个字符");
        }
        if (userDao.findByUsername(username) != null) {
            throw BusinessException.badRequest("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        String nickname = trim(request.getNickname());
        user.setNickname(nickname.isEmpty() ? username : nickname);
        user.setEmail(trim(request.getEmail()));
        user.setStatus(1);
        // 前台注册用户默认为订阅者（仅可评论与维护个人资料）
        user.setRole(UserRole.SUBSCRIBER);
        user.setDeleted(0);
        userDao.insert(user);
        return UserVO.from(user);
    }

    @Override
    public boolean adminExists() {
        return userDao.countActiveAdmins() > 0;
    }

    @Override
    public LoginVO registerAdmin(AdminRegisterRequest request) {
        ADMIN_BOOTSTRAP_LOCK.lock();
        try {
            if (adminExists()) {
                throw BusinessException.badRequest("管理员用户已存在，无法重复注册");
            }

            String email = validateAdminEmail(request.getEmail());
            String username = validateUsername(request.getUsername());
            String password = validatePassword(request.getPassword());

            User user = new User();
            user.setUsername(username);
            user.setPassword(PasswordUtil.encode(password));
            String nickname = trim(request.getNickname());
            user.setNickname(nickname.isEmpty() ? username : nickname);
            user.setEmail(email);
            user.setStatus(1);
            user.setRole(UserRole.ADMIN);
            user.setDeleted(0);
            userDao.insert(user);
            return new LoginVO(jwtUtil.createToken(user.getId()), UserVO.from(user));
        } finally {
            ADMIN_BOOTSTRAP_LOCK.unlock();
        }
    }

    @Override
    public LoginVO login(LoginRequest request) {
        String username = trim(request.getUsername());
        String password = request.getPassword();
        if (username.isEmpty() || password == null || password.isEmpty()) {
            throw BusinessException.badRequest("用户名和密码不能为空");
        }
        User user = userDao.findByUsername(username);
        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            throw BusinessException.badRequest("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw BusinessException.forbidden("账号已被禁用");
        }
        return new LoginVO(jwtUtil.createToken(user.getId()), UserVO.from(user));
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("登录状态无效，请重新登录");
        }
        return UserVO.from(user);
    }

    @Override
    public PageResult<UserVO> page(Integer page, Integer pageSize, String keyword) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String trimmedKeyword = keyword == null || keyword.trim().isEmpty() ? null : keyword.trim();
        long total = userDao.countByKeyword(trimmedKeyword);
        List<UserVO> records = new ArrayList<>();
        if (total > 0) {
            userDao.pageList(trimmedKeyword, (currentPage - 1) * currentSize, currentSize)
                    .forEach(user -> records.add(UserVO.from(user)));
        }
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public UserVO createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(validateUsername(request.getUsername()));
        user.setPassword(PasswordUtil.encode(validatePassword(request.getPassword())));
        String nickname = trim(request.getNickname());
        user.setNickname(nickname.isEmpty() ? user.getUsername() : nickname);
        user.setEmail(validateOptional(request.getEmail(), "邮箱", EMAIL_MAX_LENGTH));
        Integer status = request.getStatus();
        if (status == null) {
            status = 1;
        }
        if (status != 0 && status != 1) {
            throw BusinessException.badRequest("用户状态不合法");
        }
        user.setStatus(status);
        // 后台添加的用户默认为作者，可在用户管理中调整角色
        user.setRole(UserRole.AUTHOR);
        user.setDeleted(0);
        userDao.insert(user);
        return UserVO.from(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw BusinessException.badRequest("用户状态不合法");
        }
        if (userDao.findById(id) == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        userDao.updateStatus(id, status);
    }

    @Override
    public UserVO updateProfile(Long userId, ProfileUpdateRequest request) {
        User existing = userDao.findById(userId);
        if (existing == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        User user = new User();
        user.setId(userId);
        String nickname = trim(request.getNickname());
        if (nickname.isEmpty()) {
            throw BusinessException.badRequest("昵称不能为空");
        }
        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            throw BusinessException.badRequest("昵称不能超过 50 个字符");
        }
        user.setNickname(nickname);
        user.setEmail(validateOptional(request.getEmail(), "邮箱", EMAIL_MAX_LENGTH));
        user.setAvatar(validateOptional(request.getAvatar(), "头像地址", AVATAR_MAX_LENGTH));
        userDao.updateProfile(user);
        return UserVO.from(userDao.findById(userId));
    }

    @Override
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User existing = userDao.findById(userId);
        if (existing == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        String oldPassword = request.getOldPassword() == null ? "" : request.getOldPassword();
        if (!PasswordUtil.matches(oldPassword, existing.getPassword())) {
            throw BusinessException.badRequest("原密码不正确");
        }
        String newPassword = validatePassword(request.getNewPassword());
        if (PasswordUtil.matches(newPassword, existing.getPassword())) {
            throw BusinessException.badRequest("新密码不能与原密码相同");
        }
        userDao.updatePassword(userId, PasswordUtil.encode(newPassword));
    }

    private String validateUsername(String username) {
        String trimmed = trim(username);
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("用户名不能为空");
        }
        if (trimmed.length() < USERNAME_MIN_LENGTH || trimmed.length() > USERNAME_MAX_LENGTH) {
            throw BusinessException.badRequest("用户名长度必须为 4-50 个字符");
        }
        if (userDao.findByUsername(trimmed) != null) {
            throw BusinessException.badRequest("用户名已存在");
        }
        return trimmed;
    }

    @Override
    public boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        User user = userDao.findById(userId);
        return user != null && Integer.valueOf(UserRole.ADMIN).equals(user.getRole());
    }

    @Override
    public void updateRole(Long id, Integer role) {
        permissionService.requireAdmin();
        if (!UserRole.isValid(role)) {
            throw BusinessException.badRequest("角色不合法（1管理员，2作者，3订阅者）");
        }
        User existing = userDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        if (Integer.valueOf(UserRole.ADMIN).equals(existing.getRole())
                && role != UserRole.ADMIN
                && id.equals(UserContext.getUserId())) {
            throw BusinessException.badRequest("不能取消自己的管理员权限");
        }
        userDao.updateRole(id, role);
    }

    @Override
    public void deleteUser(Long id) {
        permissionService.requireAdmin();
        User existing = userDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        if (id.equals(UserContext.getUserId())) {
            throw BusinessException.badRequest("不能删除自己的账号");
        }
        userDao.softDelete(id);
    }

    @Override
    public UserVO updateBlogger(Long userId, BloggerInfoRequest request) {
        // 博主信息仅对管理员与作者开放
        permissionService.requireArticleWrite();
        User existing = userDao.findById(userId);
        if (existing == null) {
            throw BusinessException.notFound("用户不存在或已被删除");
        }
        String bloggerName = validateOptional(request.getBloggerName(), "博主名字", NICKNAME_MAX_LENGTH);
        String bloggerAvatar = validateOptional(request.getBloggerAvatar(), "博主头像地址", AVATAR_MAX_LENGTH);
        userDao.updateBlogger(userId, bloggerName, bloggerAvatar);

        // 管理员（站点所有者）保存时同步站点级博主信息，供首页用户信息模块与博主页面使用
        if (Integer.valueOf(UserRole.ADMIN).equals(existing.getRole())) {
            String ownerName = bloggerName.isEmpty()
                    ? siteSettingService.getSettings().getAuthorName() : bloggerName;
            siteSettingService.updateAuthorInfo(ownerName, bloggerAvatar);
        }
        return UserVO.from(userDao.findById(userId));
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw BusinessException.badRequest("密码长度必须为 6-50 个字符");
        }
        return password;
    }

    private String validateOptional(String value, String fieldName, int maxLength) {
        String trimmed = trim(value);
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    /**
     * 首次管理员注册邮箱必填，并统一转小写保存。
     */
    private String validateAdminEmail(String email) {
        String normalized = trim(email).toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw BusinessException.badRequest("邮箱不能为空");
        }
        if (normalized.length() > EMAIL_MAX_LENGTH) {
            throw BusinessException.badRequest("邮箱不能超过 100 个字符");
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw BusinessException.badRequest("邮箱格式不正确");
        }
        return normalized;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
