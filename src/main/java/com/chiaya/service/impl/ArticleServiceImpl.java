package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.ErrorCode;
import com.chiaya.common.ExtraViewLevel;
import com.chiaya.common.PageResult;
import com.chiaya.dao.*;
import com.chiaya.context.UserContext;
import com.chiaya.dto.ArticleRequest;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.Category;
import com.chiaya.pojo.Media;
import com.chiaya.pojo.Tag;
import com.chiaya.service.ArticleService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文章业务实现。
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final int TITLE_MAX_LENGTH = 200;

    private static final int SUMMARY_MAX_LENGTH = 500;

    private static final int SUMMARY_AUTO_LENGTH = 100;

    /** 文章状态：0草稿，1待审核，2已发布 */
    private static final int STATUS_DRAFT = 0;

    private static final int STATUS_PENDING = 1;

    private static final int STATUS_PUBLISHED = 2;

    private static final int COVER_IMAGE_MAX_LENGTH = 500;

    private final ArticleDao articleDao;


    private final ArticleTagDao articleTagDao;

    private final ArticleExtraFileDao articleExtraFileDao;

    private final CommentDao commentDao;

    private final CategoryDao categoryDao;

    private final TagDao tagDao;

    private final MediaDao mediaDao;

    private final PermissionService permissionService;

    public ArticleServiceImpl(ArticleDao articleDao, ArticleTagDao articleTagDao,
                              ArticleExtraFileDao articleExtraFileDao, CommentDao commentDao, CategoryDao categoryDao,
                              TagDao tagDao, MediaDao mediaDao, PermissionService permissionService) {
        this.articleDao = articleDao;

        this.articleTagDao = articleTagDao;
        this.articleExtraFileDao = articleExtraFileDao;
        this.commentDao = commentDao;
        this.categoryDao = categoryDao;
        this.tagDao = tagDao;
        this.mediaDao = mediaDao;
        this.permissionService = permissionService;
    }

    @Override
    public PageResult<Article> page(Integer page, Integer pageSize, String keyword, Long categoryId, Long tagId,
                                    Long authorId) {
        // 博客前台只展示「已发布」的文章
        return queryPage(page, pageSize, keyword, categoryId, tagId, STATUS_PUBLISHED, authorId);
    }

    @Override
    public PageResult<Article> pageForAdmin(Integer page, Integer pageSize, String keyword, Integer status,
                                            Long categoryId, Long tagId) {
        if (status != null && (status < STATUS_DRAFT || status > STATUS_PUBLISHED)) {
            throw BusinessException.badRequest("文章状态不合法");
        }
        // 数据范围：管理员可查看全部作者的文章，其他账号仅能查看本人文章
        return queryPage(page, pageSize, keyword, categoryId, tagId, status, articleScopeAuthorId());
    }

    @Override
    public ArticleStatsVO stats() {
        return articleDao.stats(articleScopeAuthorId());
    }

    /**
     * 文章管理接口的数据范围。
     *
     * <p>管理员不受作者限制（返回 null 表示全部作者）；其他角色只能操作本人数据，
     * 作者 ID 由登录态（JWT 解析后的 UserContext）决定，不接受前端传参，避免越权查看他人文章。</p>
     */
    private Long articleScopeAuthorId() {
        if (permissionService.isAdmin()) {
            return null;
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return userId;
    }

    private PageResult<Article> queryPage(Integer page, Integer pageSize, String keyword,
                                          Long categoryId, Long tagId, Integer status, Long authorId) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String trimmedKeyword = keyword == null ? null : keyword.trim();
        if (trimmedKeyword != null && trimmedKeyword.isEmpty()) {
            trimmedKeyword = null;
        }
        long total = articleDao.countByKeyword(trimmedKeyword, categoryId, tagId, status, authorId);
        List<Article> records = total == 0
                ? List.of()
                : articleDao.pageList(trimmedKeyword, categoryId, tagId, status, authorId,
                (currentPage - 1) * currentSize, currentSize);
        fillTags(records);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public Article getById(Long id) {
        Article article = articleDao.findById(id);
        if (article == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        // 未登录访客不能查看草稿 / 待审核文章
        if (article.getStatus() != null && article.getStatus() != STATUS_PUBLISHED
                && UserContext.getUserId() == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        articleDao.increaseViewCount(id);
        article.setViewCount(article.getViewCount() == null ? 1 : article.getViewCount() + 1);
        article.setTags(articleTagDao.findTagsByArticleId(id));
        List<ExtraFileVO> extraFiles = articleExtraFileDao.findByArticleId(id);
        Integer extraViewLevel = article.getExtraViewLevel() == null
                ? ExtraViewLevel.DEFAULT_LEVEL : article.getExtraViewLevel();
        boolean hasExtra = (article.getExtraContent() != null && !article.getExtraContent().isBlank())
                || !extraFiles.isEmpty();
        if (permissionService.canViewExtraContent(extraViewLevel)) {
            article.setExtraFiles(extraFiles);
            article.setExtraLocked(false);
        } else {
            article.setExtraContent(null);
            article.setExtraFiles(List.of());
            article.setExtraLocked(hasExtra);
        }
        return article;
    }

    @Override
    public Long create(Long userId, ArticleRequest request) {
        Article article = new Article();
        article.setUserId(userId);
        article.setCategoryId(resolveCategoryId(request.getCategoryId()));
        article.setTitle(validateTitle(request.getTitle()));
        String content = validateContent(request.getContent());
        article.setContent(content);
        article.setExtraContent(resolveExtraContent(request.getExtraContent()));
        article.setExtraViewLevel(resolveExtraViewLevel(request.getExtraViewLevel()));
        article.setSummary(resolveSummary(request.getSummary(), content));
        article.setCoverImage(resolveCoverImage(request.getCoverImage()));
        article.setStatus(resolveStatus(request.getStatus(), permissionService.isAdmin()));
        article.setAllowComment(resolveAllowComment(request.getAllowComment()));
        article.setViewCount(0);
        article.setDeleted(0);
        articleDao.insert(article);
        saveTags(article.getId(), request.getTagIds());
        saveExtraFiles(article.getId(), request.getExtraFileIds());
        return article.getId();
    }

    @Override
    public void update(Long userId, Long id, ArticleRequest request) {
        Article existing = articleDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        if (!existing.getUserId().equals(userId)) {
            throw BusinessException.forbidden("只能编辑自己发布的文章");
        }
        Article article = new Article();
        article.setId(id);
        article.setUserId(userId);
        article.setCategoryId(resolveCategoryId(request.getCategoryId()));
        article.setTitle(validateTitle(request.getTitle()));
        String content = validateContent(request.getContent());
        article.setContent(content);
        article.setExtraContent(resolveExtraContent(request.getExtraContent()));
        article.setExtraViewLevel(resolveExtraViewLevel(request.getExtraViewLevel()));
        article.setSummary(resolveSummary(request.getSummary(), content));
        article.setCoverImage(resolveCoverImage(request.getCoverImage()));
        article.setStatus(resolveStatus(request.getStatus(), permissionService.isAdmin()));
        article.setAllowComment(resolveAllowComment(request.getAllowComment()));
        if (articleDao.update(article) == 0) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        articleTagDao.deleteByArticleId(id);
        saveTags(id, request.getTagIds());
        articleExtraFileDao.deleteByArticleId(id);
        saveExtraFiles(id, request.getExtraFileIds());
    }

    /**
     * 是否允许评论：未指定时默认允许。
     */
    private Integer resolveAllowComment(Boolean allowComment) {
        return allowComment == null || allowComment ? 1 : 0;
    }

    /**
     * 文章状态解析：
     * 管理员可自由设置 草稿 / 待审核 / 已发布；
     * 作者只能保存草稿，或提交为待审核（发布需管理员审核通过后才会出现在博客）。
     */
    private Integer resolveStatus(Integer status, boolean admin) {
        int target = status == null ? (admin ? STATUS_PUBLISHED : STATUS_PENDING) : status;
        if (target < STATUS_DRAFT || target > STATUS_PUBLISHED) {
            throw BusinessException.badRequest("文章状态不合法");
        }
        if (admin) {
            return target;
        }
        return target == STATUS_DRAFT ? STATUS_DRAFT : STATUS_PENDING;
    }

    /**
     * 封面图片地址：为空时存 null。
     */
    private String resolveCoverImage(String coverImage) {
        String trimmed = coverImage == null ? "" : coverImage.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > COVER_IMAGE_MAX_LENGTH) {
            throw BusinessException.badRequest("封面图片地址过长");
        }
        return trimmed;
    }

    /**
     * 附加内容：为空时存 null（选填字段）。
     */
    private String resolveExtraContent(String extraContent) {
        String trimmed = extraContent == null ? "" : extraContent.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 附加内容查看权限：为空时默认「订阅者及以上」。
     */
    private Integer resolveExtraViewLevel(Integer extraViewLevel) {
        if (extraViewLevel == null) {
            return ExtraViewLevel.DEFAULT_LEVEL;
        }
        if (!ExtraViewLevel.isValid(extraViewLevel)) {
            throw BusinessException.badRequest("附加内容查看权限不合法");
        }
        return extraViewLevel;
    }

    /**
     * 校验并保存附加内容附件（媒体库文件）。
     */
    private void saveExtraFiles(Long articleId, List<Long> extraFileIds) {
        List<Long> validMediaIds = resolveExtraFileIds(extraFileIds);
        if (!validMediaIds.isEmpty()) {
            articleExtraFileDao.insertBatch(articleId, validMediaIds);
        }
    }

    /**
     * 附件去重并校验媒体库文件是否存在。
     */
    private List<Long> resolveExtraFileIds(List<Long> extraFileIds) {
        if (extraFileIds == null || extraFileIds.isEmpty()) {
            return List.of();
        }
        Set<Long> distinctIds = new LinkedHashSet<>();
        for (Long mediaId : extraFileIds) {
            if (mediaId != null) {
                distinctIds.add(mediaId);
            }
        }
        if (distinctIds.isEmpty()) {
            return List.of();
        }
        List<Long> idList = new ArrayList<>(distinctIds);
        List<Media> medias = mediaDao.findByIds(idList);
        if (medias.size() != idList.size()) {
            throw BusinessException.badRequest("附加内容附件中存在无效文件");
        }
        return idList;
    }

    /**
     * 批量装配列表页文章的标签，避免逐条查询。
     */
    private void fillTags(List<Article> articles) {
        if (articles.isEmpty()) {
            return;
        }
        List<Long> articleIds = new ArrayList<>(articles.size());
        for (Article article : articles) {
            articleIds.add(article.getId());
        }
        List<ArticleTagItem> items = articleTagDao.findTagsByArticleIds(articleIds);
        Map<Long, List<TagVO>> tagMap = new HashMap<>();
        for (ArticleTagItem item : items) {
            tagMap.computeIfAbsent(item.getArticleId(), key -> new ArrayList<>())
                    .add(new TagVO(item.getTagId(), item.getTagName()));
        }
        for (Article article : articles) {
            article.setTags(tagMap.getOrDefault(article.getId(), List.of()));
        }
    }

    /**
     * 校验并保存文章标签关系。
     */
    private void saveTags(Long articleId, List<Long> tagIds) {
        List<Long> validTagIds = resolveTagIds(tagIds);
        if (!validTagIds.isEmpty()) {
            articleTagDao.insertBatch(articleId, validTagIds);
        }
    }

    private Long resolveCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryDao.findById(categoryId);
        if (category == null) {
            throw BusinessException.badRequest("所选分类不存在或已被删除");
        }
        return categoryId;
    }

    private List<Long> resolveTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        Set<Long> distinctIds = new LinkedHashSet<>();
        for (Long tagId : tagIds) {
            if (tagId != null) {
                distinctIds.add(tagId);
            }
        }
        if (distinctIds.isEmpty()) {
            return List.of();
        }
        List<Long> idList = new ArrayList<>(distinctIds);
        List<Tag> tags = tagDao.findByIds(idList);
        if (tags.size() != idList.size()) {
            throw BusinessException.badRequest("标签列表中存在无效标签");
        }
        return idList;
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Article existing = articleDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        // 管理员可以删除所有文章；作者只能删除自己的文章
        if (!permissionService.isAdmin() && !existing.getUserId().equals(userId)) {
            throw BusinessException.forbidden("只能删除自己发布的文章");
        }
        if (articleDao.softDelete(id, existing.getUserId()) == 0) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        // 文章删除时，该文章下的评论一并删除（移入评论回收站，保留 1 天后自动彻底删除）
        commentDao.softDeleteByArticleId(id);
    }

    @Override
    public void updateStatusByAdmin(Long id, Integer status) {
        if (status == null || status < STATUS_DRAFT || status > STATUS_PUBLISHED) {
            throw BusinessException.badRequest("文章状态不合法");
        }
        if (articleDao.findById(id) == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        if (articleDao.updateStatus(id, status) == 0) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
    }
    @Override
    public Media requireExtraFileAccess(Long articleId, Long mediaId) {
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw BusinessException.notFound("文章不存在或已被删除");
        }
        List<ExtraFileVO> extraFiles = articleExtraFileDao.findByArticleId(articleId);
        boolean belongs = extraFiles.stream().anyMatch(file -> file.getMediaId().equals(mediaId));
        if (!belongs) {
            throw BusinessException.notFound("附件不存在或已被删除");
        }
        Integer extraViewLevel = article.getExtraViewLevel() == null
                ? ExtraViewLevel.DEFAULT_LEVEL : article.getExtraViewLevel();
        if (!permissionService.canViewExtraContent(extraViewLevel)) {
            throw BusinessException.forbidden("当前权限不足");
        }
        Media media = mediaDao.findById(mediaId);
        if (media == null) {
            throw BusinessException.notFound("附件不存在或已被删除");
        }
        return media;
    }

    private String validateTitle(String title) {
        String trimmed = title == null ? "" : title.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("文章标题不能为空");
        }
        if (trimmed.length() > TITLE_MAX_LENGTH) {
            throw BusinessException.badRequest("文章标题不能超过 200 个字符");
        }
        return trimmed;
    }

    private String validateContent(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("文章内容不能为空");
        }
        return trimmed;
    }

    private String resolveSummary(String summary, String content) {
        String trimmed = summary == null ? "" : summary.trim();
        if (trimmed.isEmpty()) {
            trimmed = content.length() > SUMMARY_AUTO_LENGTH ? content.substring(0, SUMMARY_AUTO_LENGTH) : content;
        }
        return trimmed.length() > SUMMARY_MAX_LENGTH ? trimmed.substring(0, SUMMARY_MAX_LENGTH) : trimmed;
    }
}
