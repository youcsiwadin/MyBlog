package com.chiaya.service;

import com.chiaya.vo.BlogSidebarVO;
import com.chiaya.vo.BlogAuthorVO;

/**
 * 博客站点业务接口。
 */
public interface BlogService {

    /**
     * 查询博客站点左侧栏数据。
     */
    BlogSidebarVO sidebar();

    /**
     * 查询指定作者的信息（用于文章作者模块与作者页面）。
     */
    BlogAuthorVO author(Long userId);
}
