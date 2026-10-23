package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.context.UserContext;
import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.CommentDao;
import com.chiaya.dao.SiteSettingDao;
import com.chiaya.dto.CommentRequest;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.Comment;
import com.chiaya.service.CommentService;
import com.chiaya.common.UserRole;
import com.chiaya.service.SiteSettingService;
import com.chiaya.service.PermissionService;
import com.chiaya.service.UserService;
import com.chiaya.vo.CommentStatsVO;
import com.chiaya.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论业务实现。
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final int EMAIL_MAX_LENGTH = 100;

    private static final int CONTENT_MAX_LENGTH = 1000;

    private static final int STATUS_PENDING = 0;

    private static final int STATUS_APPROVED = 1;

    private static final int STATUS_REJECTED = 2;

    /** 「评论自动过审」开关在 site_setting 中的配置键：1 开启，0 关闭 */
    private static final String KEY_COMMENT_AUTO_APPROVE = "comment_auto_approve";

    private static final String AUTO_APPROVE_ON = "1";

    private final CommentDao commentDao;

    private final ArticleDao articleDao;

    private final SiteSettingDao siteSettingDao;

    private final UserService userService;

    private final PermissionService permissionService;

    private final SiteSettingService siteSettingService;

    public CommentServiceImpl(CommentDao commentDao, ArticleDao articleDao, SiteSettingDao siteSettingDao,
                              UserService userService, PermissionService permissionService,
                              SiteSettingService siteSettingService) {
        this.commentDao = commentDao;
        this.articleDao = articleDao;
        this.siteSettingDao = siteSettingDao;
        this.userService = userService;
        this.permissionService = permissionService;
        this.siteSettingService = siteSettingService;
    }

    @Override
    public PageResult<Comment> page(Integer page, Integer pageSize, Integer status, String keyword,
                                    Integer deleted, Boolean mine) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        if (status != null && (status < STATUS_PENDING || status > STATUS_REJECTED)) {
            throw BusinessException.badRequest("评论状态不合法");
        }
        int deletedFlag = deleted != null && deleted == 1 ? 1 : 0;
        Long userId = null;
        if (!permissionService.isAdmin()) {
            // 非管理员只能查看自己的评论（作者 / 订阅者）
            userId = UserContext.getUserId();
            if (userId == null) {
                throw BusinessException.unauthorized("请先登录");
            }
        } else if (Boolean.TRUE.equals(mine)) {
            userId = UserContext.getUserId();
        }
        String trimmedKeyword = keyword == null || keyword.trim().isEmpty() ? null : keyword.trim();
        long total = commentDao.countByCondition(status, trimmedKeyword, deletedFlag, userId);
        List<Comment> records = total == 0
                ? List.of()
                : commentDao.pageList(status, trimmedKeyword, deletedFlag, userId,
                (currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public CommentStatsVO stats() {
        return commentDao.stats(UserContext.getUserId());
    }

    @Override
    public Long create(CommentRequest request) {
        // 评论提交必须登录；未登录访客只能查看评论。
        permissionService.requireLogin();
        if (request.getArticleId() == null) {
            throw BusinessException.badRequest("缺少文章ID");
        }
        Article article = articleDao.findById(request.getArticleId());
        if (article == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        if (article.getAllowComment() != null && article.getAllowComment() == 0) {
            throw BusinessException.badRequest("该文章已关闭评论");
        }
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        Long userId = UserContext.getUserId();
        comment.setUserId(userId);
        comment.setNickname(resolveNickname(userId));
        comment.setEmail(validateOptional(request.getEmail(), "邮箱", EMAIL_MAX_LENGTH));
        comment.setContent(validateRequired(request.getContent(), "评论内容", CONTENT_MAX_LENGTH));
        // 默认进入待审核；开启「自动过审」时直接通过，无需人工审核
        comment.setStatus(isAutoApproveEnabled() ? STATUS_APPROVED : STATUS_PENDING);
        commentDao.insert(comment);
        return comment.getId();
    }

    @Override
    public boolean isAutoApproveEnabled() {
        return AUTO_APPROVE_ON.equals(siteSettingDao.findValueByKey(KEY_COMMENT_AUTO_APPROVE));
    }

    @Override
    public boolean updateAutoApprove(boolean enabled) {
        siteSettingDao.upsert(KEY_COMMENT_AUTO_APPROVE, enabled ? AUTO_APPROVE_ON : "0");
        return enabled;
    }

    /**
     * 评论者名称规则：
     * 评论提交必须登录，评论者名称由账号权限决定，不能在评论中自行修改——
     * 作者（及管理员）默认使用博主名字，订阅者使用本人昵称。
     */
    private String resolveNickname(Long userId) {
        Integer role = permissionService.currentRole();
        if (role != null && (role == UserRole.ADMIN || role == UserRole.AUTHOR)) {
            String authorName = siteSettingService.getSettings().getAuthorName();
            if (authorName != null && !authorName.isBlank()) {
                return authorName.trim();
            }
        }
        UserVO user = userService.getCurrentUser(userId);
        String nickname = user.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = user.getUsername();
        }
        return nickname;
    }

    @Override
    public PageResult<Comment> pageByArticle(Long articleId, Integer page, Integer pageSize) {
        if (articleId == null || articleDao.findById(articleId) == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        long total = commentDao.countByArticle(articleId);
        List<Comment> records = total == 0
                ? List.of()
                : commentDao.pageListByArticle(articleId, (currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != STATUS_APPROVED && status != STATUS_REJECTED)) {
            throw BusinessException.badRequest("评论审核状态不合法");
        }
        if (commentDao.findById(id) == null) {
            throw BusinessException.notFound("评论不存在或已被删除");
        }
        commentDao.updateStatus(id, status);
    }

    @Override
    public void delete(Long id) {
        if (commentDao.findById(id) == null) {
            throw BusinessException.notFound("评论不存在或已被删除");
        }
        commentDao.softDelete(id);
    }

    @Override
    public void restore(Long id) {
        if (commentDao.restore(id) == 0) {
            throw BusinessException.notFound("该评论不在回收站中");
        }
    }

    @Override
    public void purge(Long id) {
        if (commentDao.purge(id) == 0) {
            throw BusinessException.notFound("该评论不在回收站中，无法彻底删除");
        }
    }

    @Override
    public int purgeExpired() {
        return commentDao.purgeExpired();
    }

    @Override
    public long countPending() {
        // 待审核评论数量属于管理信息，仅管理员权限可查看
        if (!userService.isAdmin(UserContext.getUserId())) {
            throw BusinessException.forbidden("需要管理员权限");
        }
        return commentDao.countByStatus(STATUS_PENDING);
    }

    private String validateRequired(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest(fieldName + "不能为空");
        }
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    private String validateOptional(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }
}
