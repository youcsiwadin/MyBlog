package com.chiaya.service;

import com.chiaya.common.BusinessException;
import com.chiaya.common.ExtraViewLevel;
import com.chiaya.common.UserRole;
import com.chiaya.context.UserContext;
import com.chiaya.dao.UserDao;
import com.chiaya.pojo.User;
import org.springframework.stereotype.Service;

/**
 * 权限校验服务：根据当前登录用户的角色判断是否有权执行操作。
 *
 * <p>角色：1管理员（全部功能）、2作者（文章 / 我的评论 / 个人资料）、
 * 3订阅者（我的评论 / 个人资料）。未登录访客不属于用户角色。</p>
 */
@Service
public class PermissionService {

    private final UserDao userDao;

    public PermissionService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 当前登录用户的角色，未登录返回 null。
     */
    public Integer currentRole() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return null;
        }
        User user = userDao.findById(userId);
        return user == null ? null : user.getRole();
    }

    public boolean isAdmin() {
        return Integer.valueOf(UserRole.ADMIN).equals(currentRole());
    }

    /**
     * 要求管理员权限（角色 1）。
     */
    public void requireAdmin() {
        Integer role = currentRole();
        if (role == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (role != UserRole.ADMIN) {
            throw BusinessException.forbidden("需要管理员权限");
        }
    }

    /**
     * 要求"可写文章"权限（管理员或作者）。
     */
    public void requireArticleWrite() {
        Integer role = currentRole();
        if (role == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (role != UserRole.ADMIN && role != UserRole.AUTHOR) {
            throw BusinessException.forbidden("需要作者及以上权限");
        }
    }

    /**
     * 要求已登录（任意角色）。
     */
    public void requireLogin() {
        if (currentRole() == null) {
            throw BusinessException.unauthorized("请先登录");
        }
    }

    /**
     * 当前用户是否可以查看指定权限等级的附加内容。
     *
     * <p>角色数值越小权限越高（1管理员 &lt; 2作者 &lt; 3订阅者），
     * 因此当用户角色小于等于要求的等级时可以查看；未登录用户没有角色，始终不可查看。</p>
     *
     * @param requiredLevel 附加内容查看权限等级，见 {@link com.chiaya.common.ExtraViewLevel}
     */
    public boolean canViewExtraContent(Integer requiredLevel) {
        if (!ExtraViewLevel.isValid(requiredLevel)) {
            return false;
        }
        Integer role = currentRole();
        return role != null && role <= requiredLevel;
    }

    /**
     * 要求可进入博客管理系统（管理员 / 作者 / 订阅者）。
     */
    public void requireAdminSystem() {
        Integer role = currentRole();
        if (role == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (role != UserRole.ADMIN && role != UserRole.AUTHOR && role != UserRole.SUBSCRIBER) {
            throw BusinessException.forbidden("当前账号无法使用博客管理系统");
        }
    }
}
