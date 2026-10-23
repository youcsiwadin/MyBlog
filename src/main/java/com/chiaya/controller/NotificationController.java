package com.chiaya.controller;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.context.UserContext;
import com.chiaya.dao.ArticleDao;
import com.chiaya.pojo.Article;
import com.chiaya.service.NotificationCacheService;
import com.chiaya.service.NotificationService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.LikeNotificationDetailVO;
import com.chiaya.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PermissionService permissionService;
    private final NotificationCacheService notificationCacheService;
    private final ArticleDao articleDao;

    /**
     * 分页查询当前用户通知
     * GET /api/notifications?page=1&pageSize=10&type=1&isRead=0
     */
    @GetMapping
    public Result<PageResult<NotificationVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead) {
        permissionService.requireLogin();
        Long userId = UserContext.getUserId();
        return Result.success(notificationService.page(userId, page, pageSize, type, isRead));
    }

    /**
     * 未读数量
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        permissionService.requireLogin();
        return Result.success(notificationService.countUnread(UserContext.getUserId()));
    }

    /**
     * 标记单条已读
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        permissionService.requireLogin();
        notificationService.markRead(id, UserContext.getUserId());
        return Result.success("已标记为已读", null);
    }

    /**
     * 全部标记已读
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        permissionService.requireLogin();
        notificationService.markAllRead(UserContext.getUserId());
        return Result.success("全部已读", null);
    }

    /**
     * 删除通知
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireLogin();
        notificationService.delete(id, UserContext.getUserId());
        return Result.success("删除成功", null);
    }

    /**
     * 某篇文章的未读点赞数（仅文章作者可查）
     * GET /api/notifications/article/{articleId}/unread-like-count
     */
    @GetMapping("/article/{articleId}/unread-like-count")
    public Result<Long> unreadLikeCountByArticle(@PathVariable Long articleId) {
        permissionService.requireLogin();
        Long currentUserId = UserContext.getUserId();

        Article article = articleDao.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw BusinessException.notFound("文章不存在");
        }
        if (!article.getUserId().equals(currentUserId)) {
            throw BusinessException.forbidden("只能查看自己文章的未读点赞数");
        }
        return Result.success(notificationCacheService.getArticleUnreadLike(articleId));
    }

    /**
     * ② 当前作者总未读点赞数（= 当前用户收到的未读点赞数）
     * GET /api/notifications/unread-like-count
     */
    @GetMapping("/unread-like-count")
    public Result<Long> unreadLikeCountByAuthor() {
        permissionService.requireLogin();
        Long userId = UserContext.getUserId();
        return Result.success(notificationCacheService.getAuthorUnreadLike(userId));
    }

    /**
     * ③ 某篇文章未读点赞明细（点赞人昵称 + 文章标题 + 时间）
     * GET /api/notifications/article/{articleId}/unread-like-details
     */
    @GetMapping("/article/{articleId}/unread-like-details")
    public Result<List<LikeNotificationDetailVO>> unreadLikeDetailsByArticle(
            @PathVariable Long articleId) {
        permissionService.requireLogin();
        Long currentUserId = UserContext.getUserId();

        Article article = articleDao.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw BusinessException.notFound("文章不存在");
        }
        if (!article.getUserId().equals(currentUserId)) {
            throw BusinessException.forbidden("只能查看自己文章的未读点赞明细");
        }
        return Result.success(
                notificationCacheService.getArticleUnreadLikeDetails(articleId));
    }

    /**
     * ④ 当前作者所有文章的未读点赞明细（点赞人昵称 + 文章标题 + 时间）
     * GET /api/notifications/unread-like-details
     */
    @GetMapping("/unread-like-details")
    public Result<List<LikeNotificationDetailVO>> unreadLikeDetailsByAuthor() {
        permissionService.requireLogin();
        Long userId = UserContext.getUserId();
        return Result.success(
                notificationCacheService.getAuthorUnreadLikeDetails(userId));
    }
}
