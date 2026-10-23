package com.chiaya.service.impl;

import com.chiaya.dao.NotificationDao;
import com.chiaya.service.NotificationCacheService;
import com.chiaya.vo.LikeNotificationDetailVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCacheServiceImpl implements NotificationCacheService {

    private final StringRedisTemplate redisTemplate;
    private final NotificationDao notificationDao;

    private static final String AUTHOR_COUNT_KEY   = "notify:unread:like:author:";
    private static final String ARTICLE_COUNT_KEY  = "notify:unread:like:article:";
    private static final String AUTHOR_DETAIL_KEY  = "notify:unread:like:detail:";
    private static final String ARTICLE_DETAIL_KEY = "notify:unread:like:article:detail:";
    private static final String AUTHOR_INIT_KEY    = "notify:unread:like:author:init:";
    private static final long EXPIRE_DAYS = 7;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ==================== 写入 ====================

    @Override
    public void addUnreadLike(Long authorId, Long articleId, LikeNotificationDetailVO detail) {
        String authorCountKey   = AUTHOR_COUNT_KEY + authorId;
        String articleCountKey  = ARTICLE_COUNT_KEY + articleId;
        String authorDetailKey  = AUTHOR_DETAIL_KEY + authorId;
        String articleDetailKey = ARTICLE_DETAIL_KEY + articleId;

        redisTemplate.opsForValue().increment(authorCountKey);
        redisTemplate.expire(authorCountKey, EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(articleCountKey);
        redisTemplate.expire(articleCountKey, EXPIRE_DAYS, TimeUnit.DAYS);

        // 打 init 标记，避免查询时误判"未初始化"而从 DB 重建
        redisTemplate.opsForValue().set(AUTHOR_INIT_KEY + authorId, "1",
                EXPIRE_DAYS, TimeUnit.DAYS);

        try {
            String json = MAPPER.writeValueAsString(detail);
            redisTemplate.opsForHash().put(authorDetailKey,
                    String.valueOf(detail.getNotificationId()), json);
            redisTemplate.expire(authorDetailKey, EXPIRE_DAYS, TimeUnit.DAYS);

            redisTemplate.opsForHash().put(articleDetailKey,
                    String.valueOf(detail.getNotificationId()), json);
            redisTemplate.expire(articleDetailKey, EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("写入点赞明细失败 authorId={}, articleId={}", authorId, articleId, e);
        }
    }

    // ==================== 移除 ====================

    @Override
    public void removeUnreadLike(Long authorId, Long articleId, Long notificationId) {
        decrWithFloor(AUTHOR_COUNT_KEY + authorId);
        decrWithFloor(ARTICLE_COUNT_KEY + articleId);
        redisTemplate.opsForHash().delete(AUTHOR_DETAIL_KEY + authorId,
                String.valueOf(notificationId));
        redisTemplate.opsForHash().delete(ARTICLE_DETAIL_KEY + articleId,
                String.valueOf(notificationId));
    }

    @Override
    public void clearAuthorUnreadLike(Long authorId) {
        redisTemplate.opsForValue().set(AUTHOR_COUNT_KEY + authorId, "0",
                EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(AUTHOR_INIT_KEY + authorId, "1",
                EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.delete(AUTHOR_DETAIL_KEY + authorId);
    }

    @Override
    public void clearArticleUnreadLike(Long articleId) {
        redisTemplate.opsForValue().set(ARTICLE_COUNT_KEY + articleId, "0",
                EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.delete(ARTICLE_DETAIL_KEY + articleId);
    }

    // ==================== 查询（带 DB 兜底） ====================

    @Override
    public long getArticleUnreadLike(Long articleId) {
        String val = redisTemplate.opsForValue().get(ARTICLE_COUNT_KEY + articleId);
        if (val != null) {
            return Long.parseLong(val);
        }
        long dbCount = notificationDao.countUnreadLikeByArticle(articleId);
        redisTemplate.opsForValue().set(ARTICLE_COUNT_KEY + articleId,
                String.valueOf(dbCount), EXPIRE_DAYS, TimeUnit.DAYS);
        log.info("重建文章未读点赞计数 articleId={}, count={}", articleId, dbCount);
        return dbCount;
    }

    @Override
    public long getAuthorUnreadLike(Long authorId) {
        String initKey = AUTHOR_INIT_KEY + authorId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(initKey))) {
            String val = redisTemplate.opsForValue().get(AUTHOR_COUNT_KEY + authorId);
            return val == null ? 0L : Long.parseLong(val);
        }
        long dbCount = notificationDao.countUnreadLikeByAuthor(authorId);
        redisTemplate.opsForValue().set(AUTHOR_COUNT_KEY + authorId,
                String.valueOf(dbCount), EXPIRE_DAYS, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(initKey, "1", EXPIRE_DAYS, TimeUnit.DAYS);
        log.info("重建作者未读点赞计数 authorId={}, count={}", authorId, dbCount);
        return dbCount;
    }

    @Override
    public List<LikeNotificationDetailVO> getArticleUnreadLikeDetails(Long articleId) {
        String key = ARTICLE_DETAIL_KEY + articleId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            // 回填：从 DB 查明细写入 Redis
            List<LikeNotificationDetailVO> dbList =
                    notificationDao.listUnreadLikeDetailsByArticle(articleId);
            if (!dbList.isEmpty()) {
                Map<String, String> toCache = new java.util.HashMap<>();
                for (LikeNotificationDetailVO vo : dbList) {
                    try {
                        toCache.put(String.valueOf(vo.getNotificationId()),
                                MAPPER.writeValueAsString(vo));
                    } catch (Exception ignored) { }
                }
                if (!toCache.isEmpty()) {
                    redisTemplate.opsForHash().putAll(key, toCache);
                    redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
                }
            }
            return sortDesc(dbList);
        }
        return toList(entries);
    }

    @Override
    public List<LikeNotificationDetailVO> getAuthorUnreadLikeDetails(Long authorId) {
        String key = AUTHOR_DETAIL_KEY + authorId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            List<LikeNotificationDetailVO> dbList =
                    notificationDao.listUnreadLikeDetailsByAuthor(authorId);
            if (!dbList.isEmpty()) {
                Map<String, String> toCache = new java.util.HashMap<>();
                for (LikeNotificationDetailVO vo : dbList) {
                    try {
                        toCache.put(String.valueOf(vo.getNotificationId()),
                                MAPPER.writeValueAsString(vo));
                    } catch (Exception ignored) { }
                }
                if (!toCache.isEmpty()) {
                    redisTemplate.opsForHash().putAll(key, toCache);
                    redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
                }
            }
            return sortDesc(dbList);
        }
        return toList(entries);
    }

    // ==================== 工具 ====================

    private void decrWithFloor(String key) {
        Long v = redisTemplate.opsForValue().decrement(key);
        if (v != null && v < 0) {
            redisTemplate.opsForValue().set(key, "0", EXPIRE_DAYS, TimeUnit.DAYS);
        }
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    private List<LikeNotificationDetailVO> toList(Map<Object, Object> entries) {
        List<LikeNotificationDetailVO> list = new ArrayList<>(entries.size());
        for (Object v : entries.values()) {
            try {
                list.add(MAPPER.readValue(String.valueOf(v),
                        new TypeReference<LikeNotificationDetailVO>() {}));
            } catch (Exception e) {
                log.warn("解析点赞明细失败: {}", v);
            }
        }
        return sortDesc(list);
    }

    private List<LikeNotificationDetailVO> sortDesc(List<LikeNotificationDetailVO> list) {
        list.sort(Comparator.comparing(LikeNotificationDetailVO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }
}
