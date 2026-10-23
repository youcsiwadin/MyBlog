package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.context.UserContext;
import com.chiaya.service.ArticleLikeService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.ArticleLikeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;
    private final PermissionService permissionService;

    /**
     * 点赞文章（需登录，幂等）
     * POST /api/articles/{id}/like
     */
    @PostMapping("/{id}/like")
    public Result<ArticleLikeVO> like(@PathVariable Long id) {
        permissionService.requireLogin();     // JWT 验证
        Long userId = UserContext.getUserId();
        return Result.success("点赞成功", articleLikeService.like(id, userId));
    }

    /**
     * 取消点赞（需登录，幂等）
     * DELETE /api/articles/{id}/like
     */
    @DeleteMapping("/{id}/like")
    public Result<ArticleLikeVO> unlike(@PathVariable Long id) {
        permissionService.requireLogin();
        Long userId = UserContext.getUserId();
        return Result.success("取消点赞成功", articleLikeService.unlike(id, userId));
    }
}
