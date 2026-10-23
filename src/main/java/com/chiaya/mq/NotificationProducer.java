package com.chiaya.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final StringRedisTemplate redisTemplate;

    public static final String LIKE_STREAM_KEY = "notify:stream:like";

    public void sendLikeNotification(LikeMessage msg) {
        Map<String, String> body = new HashMap<>();
        body.put("articleId", String.valueOf(msg.getArticleId()));
        body.put("articleAuthorId", String.valueOf(msg.getArticleAuthorId()));
        body.put("likeUserId", String.valueOf(msg.getLikeUserId()));
        body.put("likeUserName", msg.getLikeUserName());
        body.put("articleTitle", msg.getArticleTitle());
        body.put("timestamp", String.valueOf(msg.getTimestamp()));

        try {
            RecordId recordId = redisTemplate.opsForStream()
                    .add(StreamRecords.mapBacked(body).withStreamKey(LIKE_STREAM_KEY));
            log.info("点赞通知已发送至 RedisMQ, recordId={}", recordId);
        } catch (Exception e) {
            log.error("发送点赞通知失败", e);
            // 生产环境建议本地消息表兜底
        }
    }
}
