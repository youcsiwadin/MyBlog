package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.dto.CategoryRequest;
import com.chiaya.pojo.Category;
import com.chiaya.service.CategoryService;
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
 * 分类目录接口。
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final PermissionService permissionService;

    public CategoryController(CategoryService categoryService, PermissionService permissionService) {
        this.categoryService = categoryService;
        this.permissionService = permissionService;
    }

    /** 查询全部分类目录 GET /api/categories */
    @GetMapping
    public Result<List<Category>> list() {
        permissionService.requireArticleWrite();
        return Result.success(categoryService.list());
    }

    /** 新建分类 POST /api/categories */
    @PostMapping
    public ResponseEntity<Result<Category>> create(@RequestBody CategoryRequest request) {
        permissionService.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("分类创建成功", categoryService.create(request)));
    }

    /** 编辑分类 PUT /api/categories/{id} */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        permissionService.requireAdmin();
        return Result.success("分类更新成功", categoryService.update(id, request));
    }

    /** 删除分类 DELETE /api/categories/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.requireAdmin();
        categoryService.delete(id);
        return Result.success("分类删除成功", null);
    }
}
