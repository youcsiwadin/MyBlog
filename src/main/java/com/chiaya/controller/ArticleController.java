package com.chiaya.controller;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.context.UserContext;
import com.chiaya.dto.ArticleRequest;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.Comment;
import com.chiaya.pojo.Media;
import com.chiaya.service.ArticleLikeService;
import com.chiaya.service.ArticleService;
import com.chiaya.service.CommentService;
import com.chiaya.service.MediaService;
import com.chiaya.service.PermissionService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文章接口：分页查询、详情、新建、编辑、删除。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final PermissionService permissionService;
    private final MediaService mediaService;
    private final ArticleLikeService articleLikeService;   // ← 新增

    public ArticleController(ArticleService articleService,
                             CommentService commentService,
                             PermissionService permissionService,
                             MediaService mediaService,
                             ArticleLikeService articleLikeService) {   // ← 新增参数
        this.articleService = articleService;
        this.commentService = commentService;
        this.permissionService = permissionService;
        this.mediaService = mediaService;
        this.articleLikeService = articleLikeService;   // ← 新增
    }

    /**
     * 分页查询文章列表。
     */
    @GetMapping
    public Result<PageResult<Article>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) Long tagId,
                                            @RequestParam(required = false) Long authorId) {
        return Result.success(articleService.page(page, pageSize, keyword, categoryId, tagId, authorId));
    }

    /**
     * 查询文章详情（含当前用户是否已点赞）。
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw BusinessException.notFound("文章不存在");
        }
        // 当前登录用户是否已点赞
        Long userId = UserContext.getUserId();
        article.setLiked(userId != null && articleLikeService.isLiked(id, userId));

        return Result.success(article);
    }

    /**
     * 查询文章已通过审核的评论。
     */
    @GetMapping("/{id}/comments")
    public Result<PageResult<Comment>> comments(@PathVariable Long id,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(commentService.pageByArticle(id, page, pageSize));
    }

    @GetMapping("/{id}/extra-files/{mediaId}/download")
    public ResponseEntity<Resource> downloadExtraFile(@PathVariable Long id, @PathVariable Long mediaId) {
        Media media = articleService.requireExtraFileAccess(id, mediaId);
        String fileName = media.getFileName() == null ? "附件" : media.getFileName();
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(media.getSize() == null ? 0 : media.getSize())
                .body(mediaService.loadResource(mediaId));
    }

    @PostMapping
    public ResponseEntity<Result<Long>> create(@RequestBody ArticleRequest request) {
        permissionService.requireArticleWrite();
        Long id = articleService.create(UserContext.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("文章创建成功", id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleRequest request) {
        permissionService.requireArticleWrite();
        articleService.update(UserContext.getUserId(), id, request);
        return Result.success("文章更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireArticleWrite();
        articleService.delete(UserContext.getUserId(), id);
        return Result.success("文章删除成功", null);
    }
}
