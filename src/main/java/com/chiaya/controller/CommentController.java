package com.chiaya.controller;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.dto.CommentAutoApproveRequest;
import com.chiaya.dto.CommentRequest;
import com.chiaya.dto.CommentStatusRequest;
import com.chiaya.pojo.Comment;
import com.chiaya.service.CommentService;
import com.chiaya.service.PermissionService;
import com.chiaya.vo.CommentStatsVO;
import org.springframework.http.HttpStatus;
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

/**
 * 评论接口。
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    private final PermissionService permissionService;

    public CommentController(CommentService commentService, PermissionService permissionService) {
        this.commentService = commentService;
        this.permissionService = permissionService;
    }

    /** 分页查询评论 GET /api/comments?page=1&pageSize=10&status=&keyword= */
    @GetMapping
    public Result<PageResult<Comment>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer deleted,
                                           @RequestParam(required = false) Boolean mine) {
        permissionService.requireAdminSystem();
        return Result.success(commentService.page(page, pageSize, status, keyword, deleted, mine));
    }

    /** 评论管理筛选标签数量统计 GET /api/comments/stats */
    @GetMapping("/stats")
    public Result<CommentStatsVO> stats() {
        permissionService.requireAdmin();
        return Result.success(commentService.stats());
    }

    /** 待审核评论数量（仅管理员） GET /api/comments/pending-count */
    @GetMapping("/pending-count")
    public Result<Long> pendingCount() {
        permissionService.requireAdmin();
        return Result.success(commentService.countPending());
    }

    /** 提交评论（必须登录，默认待审核） POST /api/comments */
    @PostMapping
    public ResponseEntity<Result<Long>> create(@RequestBody CommentRequest request) {
        permissionService.requireLogin();
        Long id = commentService.create(request);
        String message = commentService.isAutoApproveEnabled()
                ? "评论提交成功，已自动通过审核"
                : "评论提交成功，等待审核";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(message, id));
    }

    /** 查询「评论自动过审」开关（仅管理员） GET /api/comments/auto-approve */
    @GetMapping("/auto-approve")
    public Result<Boolean> autoApprove() {
        permissionService.requireAdmin();
        return Result.success(commentService.isAutoApproveEnabled());
    }

    /** 开启 / 关闭「评论自动过审」（仅管理员） PUT /api/comments/auto-approve */
    @PutMapping("/auto-approve")
    public Result<Boolean> updateAutoApprove(@RequestBody CommentAutoApproveRequest request) {
        permissionService.requireAdmin();
        if (request.getEnabled() == null) {
            throw BusinessException.badRequest("缺少 enabled 参数");
        }
        boolean enabled = commentService.updateAutoApprove(request.getEnabled());
        return Result.success(enabled ? "已开启评论自动过审" : "已关闭评论自动过审", enabled);
    }

    /** 审核评论 PUT /api/comments/{id}/status */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody CommentStatusRequest request) {
        permissionService.requireAdmin();
        commentService.updateStatus(id, request.getStatus());
        return Result.success("评论状态更新成功", null);
    }

    /** 删除评论 DELETE /api/comments/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        commentService.delete(id);
        return Result.success("评论删除成功", null);
    }

    /** 从回收站恢复评论 PUT /api/comments/{id}/restore */
    @PutMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        permissionService.requireAdmin();
        commentService.restore(id);
        return Result.success("评论已恢复（状态为待审核）", null);
    }

    /** 彻底删除回收站中的评论 DELETE /api/comments/{id}/purge */
    @DeleteMapping("/{id}/purge")
    public Result<Void> purge(@PathVariable Long id) {
        permissionService.requireAdmin();
        commentService.purge(id);
        return Result.success("评论已彻底删除", null);
    }
}
