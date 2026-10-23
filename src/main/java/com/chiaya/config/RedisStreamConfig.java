package com.chiaya.config;

import com.chiaya.mq.LikeNotificationConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

import static com.chiaya.mq.NotificationProducer.LIKE_STREAM_KEY;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    private final StringRedisTemplate redisTemplate;
    private final LikeNotificationConsumer consumer;

    private static final String GROUP = "notify-group-like";
    private static final String CONSUMER_NAME = "consumer-like-1";

    @Bean
    public Subscription likeSubscription(RedisConnectionFactory factory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(factory, options);

        // 幂等创建消费者组
        try {
            redisTemplate.opsForStream().createGroup(LIKE_STREAM_KEY, ReadOffset.from("0"), GROUP);
        } catch (Exception e) {
            log.info("消费者组已存在或创建失败: {}", e.getMessage());
        }

        Subscription subscription = container.receive(
                Consumer.from(GROUP, CONSUMER_NAME),
                StreamOffset.create(LIKE_STREAM_KEY, ReadOffset.lastConsumed()),
                consumer
        );
        container.start();
        return subscription;
    }

    /**
     * 应用启动后确保 stream 与 group 存在（首次启动）
     */
    @Bean
    public ApplicationRunner streamInitRunner() {
        return args -> {
            try {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(LIKE_STREAM_KEY))) {
                    redisTemplate.opsForStream().add(LIKE_STREAM_KEY,
                            java.util.Collections.singletonMap("init", "1"));
                }
                redisTemplate.opsForStream().createGroup(LIKE_STREAM_KEY, ReadOffset.from("0"), GROUP);
            } catch (Exception ignored) {
            }
        };
    }
}
