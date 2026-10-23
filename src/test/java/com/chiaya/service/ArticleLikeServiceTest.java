package com.chiaya.service;

import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.ArticleLikeDao;
import com.chiaya.dao.NotificationDao;
import com.chiaya.dao.UserDao;
import com.chiaya.mq.NotificationProducer;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.ArticleLike;
import com.chiaya.service.impl.ArticleLikeServiceImpl;
import com.chiaya.vo.ArticleLikeVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 点赞计数回归测试。
 *
 * <p>重点验证接口返回的点赞数来自数据库最新值，而不是缓存缺失时从 0 开始的
 * Redis increment/decrement 结果。</p>
 */
@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {

    private static final Long ARTICLE_ID = 13L;
    private static final Long AUTHOR_ID = 6L;
    private static final Long LOGIN_USER_ID = 7L;
    private static final long CACHE_EXPIRE_HOURS = 24L;

    @Mock
    private ArticleLikeDao articleLikeDao;

    @Mock
    private ArticleDao articleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private NotificationCacheService notificationCacheService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private ArticleLikeServiceImpl articleLikeService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    @DisplayName("点赞后返回数据库最新点赞数，而不是从缓存 0 开始")
    void likeShouldReturnDatabaseCount() {
        Article before = article(5);
        Article after = article(6);
        when(articleDao.selectById(ARTICLE_ID)).thenReturn(before, after);
        when(articleLikeDao.selectByArticleIdAndUserId(ARTICLE_ID, AUTHOR_ID)).thenReturn(null);
        when(articleDao.incrementLikeCount(ARTICLE_ID)).thenReturn(1);

        ArticleLikeVO result = articleLikeService.like(ARTICLE_ID, AUTHOR_ID);

        assertEquals(6, result.getLikeCount());
        assertTrue(result.getLiked());
        verify(valueOperations).set(
                "article:like:count:13", "6", CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        verify(valueOperations, never()).increment("article:like:count:13");
    }

    @Test
    @DisplayName("取消点赞只减少一个，并返回数据库最新点赞数")
    void unlikeShouldReturnDatabaseCount() {
        Article before = article(6);
        Article after = article(5);
        when(articleDao.selectById(ARTICLE_ID)).thenReturn(before, after);
        when(articleLikeDao.selectByArticleIdAndUserId(ARTICLE_ID, LOGIN_USER_ID))
                .thenReturn(new ArticleLike());
        when(articleDao.decrementLikeCount(ARTICLE_ID)).thenReturn(1);
        when(notificationDao.selectLikeNotification(AUTHOR_ID, LOGIN_USER_ID, ARTICLE_ID))
                .thenReturn(null);

        ArticleLikeVO result = articleLikeService.unlike(ARTICLE_ID, LOGIN_USER_ID);

        assertEquals(5, result.getLikeCount());
        assertEquals(false, result.getLiked());
        verify(valueOperations).set(
                "article:like:count:13", "5", CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        verify(valueOperations, never()).decrement("article:like:count:13");
    }

    private Article article(int likeCount) {
        Article article = new Article();
        article.setId(ARTICLE_ID);
        article.setUserId(AUTHOR_ID);
        article.setTitle("测试文章");
        article.setLikeCount(likeCount);
        article.setDeleted(0);
        return article;
    }
}
