package com.chiaya.controller;

import com.chiaya.common.Result;
import com.chiaya.service.BlogService;
import com.chiaya.vo.BlogSidebarVO;
import com.chiaya.vo.BlogAuthorVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客站点公开接口。
 */
@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /** 博客站点左侧栏数据 GET /api/blog/sidebar */
    @GetMapping("/sidebar")
    public Result<BlogSidebarVO> sidebar() {
        return Result.success(blogService.sidebar());
    }

    /** 作者信息（文章作者模块 / 作者页面） GET /api/blog/authors/{id} */
    @GetMapping("/authors/{id}")
    public Result<BlogAuthorVO> author(@PathVariable Long id) {
        return Result.success(blogService.author(id));
    }
}
