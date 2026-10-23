package com.chiaya.controller;

import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.dto.PageRequest;
import com.chiaya.pojo.Page;
import com.chiaya.service.PageService;
import com.chiaya.service.PermissionService;
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
 * 页面接口。
 */
@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;

    private final PermissionService permissionService;

    public PageController(PageService pageService, PermissionService permissionService) {
        this.pageService = pageService;
        this.permissionService = permissionService;
    }

    /** 分页查询页面 GET /api/pages?page=1&pageSize=10&keyword=&status= */
    @GetMapping
    public Result<PageResult<Page>> page(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer status) {
        permissionService.requireAdmin();
        return Result.success(pageService.page(page, pageSize, keyword, status));
    }

    /** 页面详情 GET /api/pages/{id} */
    @GetMapping("/{id}")
    public Result<Page> detail(@PathVariable Long id) {
        permissionService.requireAdmin();
        return Result.success(pageService.getById(id));
    }

    /** 新建页面 POST /api/pages */
    @PostMapping
    public ResponseEntity<Result<Page>> create(@RequestBody PageRequest request) {
        permissionService.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("页面创建成功", pageService.create(request)));
    }

    /** 编辑页面 PUT /api/pages/{id} */
    @PutMapping("/{id}")
    public Result<Page> update(@PathVariable Long id, @RequestBody PageRequest request) {
        permissionService.requireAdmin();
        return Result.success("页面更新成功", pageService.update(id, request));
    }

    /** 删除页面 DELETE /api/pages/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        pageService.delete(id);
        return Result.success("页面删除成功", null);
    }
}
