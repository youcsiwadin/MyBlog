package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.dto.TagRequest;
import com.chiaya.pojo.Tag;
import com.chiaya.service.TagService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签接口。
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    private final PermissionService permissionService;

    public TagController(TagService tagService, PermissionService permissionService) {
        this.tagService = tagService;
        this.permissionService = permissionService;
    }

    /** 查询全部标签 GET /api/tags */
    @GetMapping
    public Result<List<Tag>> list() {
        permissionService.requireArticleWrite();
        return Result.success(tagService.list());
    }

    /** 新建标签 POST /api/tags */
    @PostMapping
    public ResponseEntity<Result<Tag>> create(@RequestBody TagRequest request) {
        permissionService.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("标签创建成功", tagService.create(request)));
    }

    /** 编辑标签 PUT /api/tags/{id} */
    @PutMapping("/{id}")
    public Result<Tag> update(@PathVariable Long id, @RequestBody TagRequest request) {
        permissionService.requireAdmin();
        return Result.success("标签更新成功", tagService.update(id, request));
    }

    /** 删除标签 DELETE /api/tags/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        tagService.delete(id);
        return Result.success("标签删除成功", null);
    }
}
