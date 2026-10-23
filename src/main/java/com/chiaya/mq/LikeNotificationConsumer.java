package com.chiaya.mq;

import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.NotificationDao;
import com.chiaya.dao.UserDao;
import com.chiaya.pojo.Article;
import com.chiaya.pojo.Notification;
import com.chiaya.pojo.User;
import com.chiaya.service.ArticleLikeService;
import com.chiaya.service.NotificationCacheService;
import com.chiaya.service.NotificationService;
import com.chiaya.vo.LikeNotificationDetailVO;
import com.chiaya.ws.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeNotificationConsumer
        implements StreamListener<String, MapRecord<String, String, String>> {

    private final NotificationService notificationService;
    private final NotificationCacheService notificationCacheService;
    private final NotificationDao notificationDao;
    private final UserDao userDao;
    private final ArticleDao articleDao;
    private final ArticleLikeService articleLikeService;
    private final StringRedisTemplate redisTemplate;
    private final NotificationWebSocketHandler wsHandler;

    private static final String STREAM_KEY = "notify:stream:like";
    private static final String GROUP      = "notify-group-like";

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        Long articleId = null;
        Long articleAuthorId = null;
        String likeUserName = null;
        String articleTitle = null;
        boolean shouldPush = false;
        LikeNotificationDetailVO likeDetail = null;

        try {
            articleId       = Long.valueOf(message.getValue().get("articleId"));
            articleAuthorId = Long.valueOf(message.getValue().get("articleAuthorId"));
            Long likeUserId = Long.valueOf(message.getValue().get("likeUserId"));

            // 自己赞自己不通知
            if (articleAuthorId.equals(likeUserId)) {
                return;
            }

            User likeUser = userDao.selectById(likeUserId);
            Article article = articleDao.selectById(articleId);
            likeUserName = likeUser == null
                    ? "某用户"
                    : (likeUser.getNickname() == null || likeUser.getNickname().isBlank()
                    ? likeUser.getUsername()
                    : likeUser.getNickname());
            articleTitle = article != null ? article.getTitle() : "某文章";

            Notification n = new Notification();
            n.setUserId(articleAuthorId);
            n.setSenderId(likeUserId);
            n.setType(1);
            n.setTargetId(articleId);
            n.setTargetType(1);
            n.setTitle("收到新的点赞");
            n.setContent(likeUserName + " 点赞了你的文章《" + articleTitle + "》");
            n.setIsRead(0);
            n.setDeleted(0);

            try {
                notificationService.saveNotification(n);
            } catch (DuplicateKeyException e) {
                log.warn("重复点赞通知，跳过 articleId={}, likeUserId={}",
                        articleId, likeUserId);
                return;
            }

            // 写 Redis
            LikeNotificationDetailVO detail = new LikeNotificationDetailVO();
            detail.setNotificationId(n.getId());
            detail.setSenderId(likeUserId);
            detail.setSenderName(likeUserName);
            detail.setSenderAvatar(likeUser != null ? likeUser.getAvatar() : null);
            detail.setArticleId(articleId);
            detail.setArticleTitle(articleTitle);
            detail.setArticleLikeCount(articleLikeService.getLikeCount(articleId));
            detail.setCreateTime(LocalDateTime.now());
            detail.setIsRead(0);
            notificationCacheService.addUnreadLike(articleAuthorId, articleId, detail);
            likeDetail = detail;

            shouldPush = true;

            log.info("点赞通知入库+Redis 完成 authorId={}, articleId={}, notifyId={}",
                    articleAuthorId, articleId, n.getId());

        } catch (Exception e) {
            log.error("处理点赞通知消息失败: {}", message, e);
        } finally {

            try {
                redisTemplate.opsForStream().acknowledge(
                        STREAM_KEY, GROUP, message.getId());
            } catch (Exception ex) {
                log.error("ACK 失败 recordId={}", message.getId(), ex);
            }

            if (shouldPush && articleAuthorId != null) {
                try {
                    long unread = notificationCacheService.getAuthorUnreadLike(articleAuthorId);
                    long unreadNotice = notificationDao.countUnread(articleAuthorId);
                    Map<String, Object> push = new HashMap<>();
                    push.put("type", "LIKE");
                    push.put("authorId", articleAuthorId);
                    push.put("unreadCount", unread);
                    push.put("unreadNoticeCount", unreadNotice);
                    push.put("notificationId", likeDetail.getNotificationId());
                    push.put("senderId", likeDetail.getSenderId());
                    push.put("senderAvatar", likeDetail.getSenderAvatar());
                    push.put("articleId", articleId);
                    push.put("senderName", likeUserName);
                    push.put("articleTitle", articleTitle);
                    push.put("articleLikeCount", likeDetail.getArticleLikeCount());
                    push.put("timestamp", System.currentTimeMillis());
                    wsHandler.sendToUser(articleAuthorId, push);
                } catch (Exception e) {
                    log.warn("WS 推送失败", e);
                }
            }
        }
    }
}
