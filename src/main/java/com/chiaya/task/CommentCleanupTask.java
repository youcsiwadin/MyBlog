package com.chiaya.task;

import com.chiaya.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 回收站定时清理任务：评论移入回收站后保留 1 天，
 * 超过保留期限的评论会被自动彻底删除。
 */
@Component
public class CommentCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(CommentCleanupTask.class);

    private final CommentService commentService;

    public CommentCleanupTask(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 启动 1 分钟后首次执行，之后每 10 分钟检查一次。
     */
    @Scheduled(initialDelay = 60_000, fixedDelay = 600_000)
    public void purgeExpiredComments() {
        try {
            int purged = commentService.purgeExpired();
            if (purged > 0) {
                log.info("回收站自动清理：已彻底删除 {} 条超过 1 天保留期限的评论", purged);
            }
        } catch (Exception ex) {
            log.error("回收站自动清理失败", ex);
        }
    }
}
