package com.chiaya.service;

import com.chiaya.common.BusinessException;
import com.chiaya.context.UserContext;
import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.ArticleExtraFileDao;
import com.chiaya.dao.ArticleTagDao;
import com.chiaya.dao.CategoryDao;
import com.chiaya.dao.MediaDao;
import com.chiaya.dao.TagDao;
import com.chiaya.common.PageResult;
import com.chiaya.pojo.Article;
import com.chiaya.service.impl.ArticleServiceImpl;
import com.chiaya.vo.ArticleStatsVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 文章管理数据范围单元测试：
 * 管理员可查看全部作者的文章，其他账号仅能查看本人文章。
 */
@ExtendWith(MockitoExtension.class)
class ArticleAdminScopeTest {

    private static final Long USER_ID = 9L;

    @Mock
    private ArticleDao articleDao;


    @Mock
    private ArticleTagDao articleTagDao;

    @Mock
    private ArticleExtraFileDao articleExtraFileDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private MediaDao mediaDao;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() {
        UserContext.setUserId(USER_ID);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    @DisplayName("管理员：文章列表不限作者")
    void adminListShouldNotFilterByAuthor() {
        when(permissionService.isAdmin()).thenReturn(true);
        when(articleDao.countByKeyword(any(), any(), any(), any(), any())).thenReturn(0L);

        PageResult<Article> result = articleService.pageForAdmin(1, 10, null, null, null, null);

        assertEquals(0, result.getTotal());
        ArgumentCaptor<Long> authorCaptor = ArgumentCaptor.forClass(Long.class);
        verify(articleDao).countByKeyword(any(), any(), any(), any(), authorCaptor.capture());
        assertNull(authorCaptor.getValue());
    }

    @Test
    @DisplayName("作者：文章列表只查本人")
    void authorListShouldFilterByCurrentUser() {
        when(permissionService.isAdmin()).thenReturn(false);
        when(articleDao.countByKeyword(any(), any(), any(), any(), any())).thenReturn(0L);

        articleService.pageForAdmin(1, 10, null, null, null, null);

        ArgumentCaptor<Long> authorCaptor = ArgumentCaptor.forClass(Long.class);
        verify(articleDao).countByKeyword(any(), any(), any(), any(), authorCaptor.capture());
        assertEquals(USER_ID, authorCaptor.getValue());
    }

    @Test
    @DisplayName("管理员：统计数量覆盖全部作者")
    void adminStatsShouldCoverAllAuthors() {
        when(permissionService.isAdmin()).thenReturn(true);
        when(articleDao.stats(null)).thenReturn(new ArticleStatsVO());

        articleService.stats();

        verify(articleDao).stats(null);
    }

    @Test
    @DisplayName("作者：统计数量只统计本人")
    void authorStatsShouldBeScopedToCurrentUser() {
        when(permissionService.isAdmin()).thenReturn(false);
        when(articleDao.stats(USER_ID)).thenReturn(new ArticleStatsVO());

        articleService.stats();

        verify(articleDao).stats(USER_ID);
    }

    @Test
    @DisplayName("非管理员未登录：拒绝访问")
    void nonAdminWithoutLoginShouldBeRejected() {
        UserContext.clear();
        when(permissionService.isAdmin()).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> articleService.stats());

        assertEquals(401, ex.getCode());
    }
}
