package com.chiaya.service;

import com.chiaya.common.PageResult;
import com.chiaya.dto.ArticleRequest;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.Media;
import com.chiaya.vo.ArticleLikeVO;
import com.chiaya.vo.ArticleStatsVO;
import com.chiaya.vo.LikeResultVO;

/**
 * 文章业务接口。
 */
public interface ArticleService {

    /**
     * 分页查询文章列表。
     */
    PageResult<Article> page(Integer page, Integer pageSize, String keyword, Long categoryId, Long tagId,
                             Long authorId);

    /**
     * 文章管理分页查询（可按状态筛选，包含草稿与待审核）。
     */
    PageResult<Article> pageForAdmin(Integer page, Integer pageSize, String keyword, Integer status,
                                    Long categoryId, Long tagId);

    /**
     * 文章管理筛选标签数量统计（数据范围由当前登录用户角色决定：
     * 管理员统计全部作者，其他角色仅统计本人）。
     */
    ArticleStatsVO stats();

    /**
     * 查询文章详情。
     */
    Article getById(Long id);

    /**
     * 新建文章，返回新文章 ID。
     */
    Long create(Long userId, ArticleRequest request);

    /**
     * 编辑文章。
     */
    void update(Long userId, Long id, ArticleRequest request);

    /**
     * 逻辑删除文章。
     */
    void delete(Long userId, Long id);

    /**
     * 修改文章状态（仅管理员，用于审核通过 / 退回）。
     */
    void updateStatusByAdmin(Long id, Integer status);
    /**
     * 校验当前用户是否可以下载该文章的附加内容附件，返回附件对应的媒体库文件。
     *
     * <p>权限不足时抛出 403，附件不属于该文章或不存在时抛出 404。</p>
     */
    Media requireExtraFileAccess(Long articleId, Long mediaId);
}
