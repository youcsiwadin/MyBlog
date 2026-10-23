package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.ArticleLikeDao;
import com.chiaya.dao.NotificationDao;
import com.chiaya.dao.UserDao;
import com.chiaya.mq.LikeMessage;
import com.chiaya.mq.NotificationProducer;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.ArticleLike;
import com.chiaya.pojo.Notification;
import com.chiaya.pojo.User;
import com.chiaya.service.ArticleLikeService;
import com.chiaya.service.NotificationCacheService;
import com.chiaya.vo.ArticleLikeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private final ArticleLikeDao articleLikeDao;
    private final ArticleDao articleDao;
    private final UserDao userDao;
    private final NotificationDao notificationDao;
    private final NotificationCacheService notificationCacheService;
    private final StringRedisTemplate redisTemplate;
    private final NotificationProducer notificationProducer;

    private static final String LIKE_COUNT_KEY = "article:like:count:";
    private static final String LIKE_USER_KEY  = "article:like:user:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    // ==================== 点赞 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleLikeVO like(Long articleId, Long userId) {
        // 1. 校验文章
        Article article = articleDao.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }

        // 2. 幂等判断：已点赞直接返回
        ArticleLike exist = articleLikeDao.selectByArticleIdAndUserId(articleId, userId);
        if (exist != null) {
            return buildVO(articleId, article.getUserId(), true);
        }

        // 3. 插入点赞记录（DB 唯一索引保证幂等）
        try {
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            like.setAuthorId(article.getUserId());
            articleLikeDao.insert(like);
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突（并发重复点赞），视为已点赞
            log.warn("重复点赞 articleId={}, userId={}", articleId, userId);
            return buildVO(articleId, article.getUserId(), true);
        }

        // 4. 更新文章点赞数
        articleDao.incrementLikeCount(articleId);

        // 5. 刷新 Redis 缓存（数据库是点赞数权威来源）
        String userSetKey = LIKE_USER_KEY + articleId;
        redisTemplate.opsForSet().add(userSetKey, String.valueOf(userId));
        redisTemplate.expire(userSetKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        // 6. 发送点赞通知（自己赞自己不通知）
        if (!article.getUserId().equals(userId)) {
            User likeUser = userDao.selectById(userId);
            String likeUserName = likeUser == null
                    ? "某用户"
                    : (likeUser.getNickname() == null || likeUser.getNickname().isBlank()
                    ? likeUser.getUsername()
                    : likeUser.getNickname());
            LikeMessage msg = new LikeMessage(
                    articleId,
                    article.getUserId(),
                    userId,
                    likeUserName,
                    article.getTitle(),
                    System.currentTimeMillis()
            );
            notificationProducer.sendLikeNotification(msg);
        }

        return buildVO(articleId, article.getUserId(), true, syncLikeCount(articleId));
    }

    // ==================== 取消点赞 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleLikeVO unlike(Long articleId, Long userId) {
        // 1. 校验文章
        Article article = articleDao.selectById(articleId);
        if (article == null || article.getDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }

        // 2. 幂等判断：未点赞直接返回
        ArticleLike exist = articleLikeDao.selectByArticleIdAndUserId(articleId, userId);
        if (exist == null) {
            return buildVO(articleId, article.getUserId(), false);
        }

        // 3. 删点赞记录
        articleLikeDao.deleteByArticleIdAndUserId(articleId, userId);

        // 4. 更新文章点赞数
        articleDao.decrementLikeCount(articleId);

        // 5. 删除对应的点赞通知，并回退未读计数
        Notification likeNotify = notificationDao.selectLikeNotification(
                article.getUserId(), userId, articleId);
        if (likeNotify != null) {
            notificationDao.deleteLikeNotification(article.getUserId(), userId, articleId);
            // 只有原本未读，才需要减 Redis 未读计数
            if (likeNotify.getIsRead() != null && likeNotify.getIsRead() == 0) {
                notificationCacheService.removeUnreadLike(
                        article.getUserId(), articleId, likeNotify.getId());
            }
        }

        // 6. 刷新 Redis 缓存（数据库是点赞数权威来源）
        String userSetKey = LIKE_USER_KEY + articleId;

        redisTemplate.opsForSet().remove(userSetKey, String.valueOf(userId));

        return buildVO(articleId, article.getUserId(), false, syncLikeCount(articleId));
    }

    // ==================== 是否已点赞 ====================

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        if (userId == null) {
            return false;
        }
        // 先查缓存
        String userSetKey = LIKE_USER_KEY + articleId;
        Boolean member = redisTemplate.opsForSet()
                .isMember(userSetKey, String.valueOf(userId));
        if (Boolean.TRUE.equals(member)) {
            return true;
        }
        // 缓存未命中，查库
        return articleLikeDao.selectByArticleIdAndUserId(articleId, userId) != null;
    }

    // ==================== 点赞数 ====================

    @Override
    public Integer getLikeCount(Long articleId) {
        return syncLikeCount(articleId);
    }

    // ==================== 工具 ====================

    private ArticleLikeVO buildVO(Long articleId, Long authorId, boolean liked) {
        return buildVO(articleId, authorId, liked, syncLikeCount(articleId));
    }

    private ArticleLikeVO buildVO(Long articleId, Long authorId, boolean liked, Integer likeCount) {
        ArticleLikeVO vo = new ArticleLikeVO();
        vo.setArticleId(articleId);
        vo.setAuthorId(authorId);
        vo.setLiked(liked);
        vo.setLikeCount(likeCount);
        return vo;
    }

    /**
     * 以数据库中的 article.like_count 为准回写 Redis。
     *
     * <p>不能直接在 Redis 上 increment/decrement：缓存键缺失时 Redis 会从 0
     * 开始计算，导致页面首次点赞显示 1、取消后显示 0，而刷新后数据库值才恢复。
     * 点赞和取消完成后统一读取数据库最新值并覆盖缓存，也能自动修复历史脏缓存。</p>
     */
    private Integer syncLikeCount(Long articleId) {
        Article latest = articleDao.selectById(articleId);
        int count;
        if (latest != null && latest.getLikeCount() != null) {
            count = latest.getLikeCount();
        } else {
            count = Math.toIntExact(articleLikeDao.countByArticleId(articleId));
        }
        redisTemplate.opsForValue().set(
                LIKE_COUNT_KEY + articleId,
                String.valueOf(count),
                CACHE_EXPIRE_HOURS,
                TimeUnit.HOURS);
        return count;
    }
}
