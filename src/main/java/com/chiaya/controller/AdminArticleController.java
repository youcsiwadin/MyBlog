package com.chiaya.controller;

import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.dto.ArticleStatusRequest;
import com.chiaya.pojo.Article;
import com.chiaya.service.ArticleService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.ArticleStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章管理接口（需登录）：可按状态筛选，包含草稿与待审核文章。
 */
@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;

    private final PermissionService permissionService;

    public AdminArticleController(ArticleService articleService, PermissionService permissionService) {
        this.articleService = articleService;
        this.permissionService = permissionService;
    }

    /**
     * 文章管理列表 GET /api/admin/articles?page=1&pageSize=10&keyword=&status=&categoryId=&tagId=
     */
    @GetMapping
    public Result<PageResult<Article>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Long tagId) {
        permissionService.requireArticleWrite();
        return Result.success(articleService.pageForAdmin(page, pageSize, keyword, status, categoryId, tagId));
    }

    /** 文章管理筛选标签数量统计 GET /api/admin/articles/stats */
    @GetMapping("/stats")
    public Result<ArticleStatsVO> stats() {
        permissionService.requireArticleWrite();
        return Result.success(articleService.stats());
    }

    /**
     * 审核文章 / 修改文章状态（仅管理员） PUT /api/admin/articles/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody ArticleStatusRequest request) {
        permissionService.requireAdmin();
        articleService.updateStatusByAdmin(id, request.getStatus());
        return Result.success("文章状态更新成功", null);
    }
}
