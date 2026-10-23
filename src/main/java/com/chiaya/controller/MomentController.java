package com.chiaya.controller;

import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.context.UserContext;
import com.chiaya.dto.MomentRequest;
import com.chiaya.pojo.Moment;
import com.chiaya.service.MomentService;
import com.chiaya.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说说接口。
 */
@RestController
@RequestMapping("/api/moments")
public class MomentController {

    private final MomentService momentService;

    private final PermissionService permissionService;

    public MomentController(MomentService momentService, PermissionService permissionService) {
        this.momentService = momentService;
        this.permissionService = permissionService;
    }

    /** 分页查询说说 GET /api/moments?page=1&pageSize=10 */
    @GetMapping
    public Result<PageResult<Moment>> page(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        permissionService.requireAdmin();
        return Result.success(momentService.page(page, pageSize));
    }

    /** 发表说说 POST /api/moments */
    @PostMapping
    public ResponseEntity<Result<Long>> create(@RequestBody MomentRequest request) {
        permissionService.requireAdmin();
        Long id = momentService.create(UserContext.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("说说发表成功", id));
    }

    /** 删除说说 DELETE /api/moments/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        momentService.delete(UserContext.getUserId(), id);
        return Result.success("说说删除成功", null);
    }
}
