
package com.chiaya.service.impl;

import com.chiaya.common.PageResult;
import com.chiaya.dao.NotificationDao;
import com.chiaya.pojo.Notification;
import com.chiaya.service.NotificationCacheService;
import com.chiaya.service.NotificationService;
import com.chiaya.vo.LikeNotificationDetailVO;
import com.chiaya.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final NotificationCacheService notificationCacheService;

    @Override
    public PageResult<NotificationVO> page(Long userId, Integer page, Integer pageSize,
                                           Integer type, Integer isRead) {
        int offset = (page - 1) * pageSize;
        List<NotificationVO> list = notificationDao.pageByUserId(
                userId, type, isRead, offset, pageSize);
        long total = notificationDao.countByUserId(userId, type, isRead);
        return new PageResult<>(list,total);
    }

    @Override
    public long countUnread(Long userId) {
        return notificationDao.countUnread(userId);
    }


    @Override
    public void saveNotification(Notification notification) {
        notificationDao.insert(notification);
    }


    @Override
    public void markRead(Long id, Long userId) {
        Notification n = notificationDao.selectById(id);
        if (n == null || !n.getUserId().equals(userId)) return;
        if (n.getIsRead() != null && n.getIsRead() == 0) {
            int rows = notificationDao.markRead(id, userId);
            if (rows > 0 && n.getType() != null && n.getType() == 1) {
                notificationCacheService.removeUnreadLike(
                        n.getUserId(), n.getTargetId(), n.getId());
            }
        }
    }

    @Override
    public void markAllRead(Long userId) {
        // 先取出当前未读明细对应的文章，再清空数据库与缓存。
        List<LikeNotificationDetailVO> unreadDetails =
                notificationCacheService.getAuthorUnreadLikeDetails(userId);
        Set<Long> articleIds = new HashSet<>();
        for (LikeNotificationDetailVO detail : unreadDetails) {
            if (detail.getArticleId() != null) {
                articleIds.add(detail.getArticleId());
            }
        }

        notificationDao.markAllRead(userId);
        notificationCacheService.clearAuthorUnreadLike(userId);
        // 作者维度清空后，文章维度的计数与明细也必须同步清空，否则单篇接口会返回旧的未读数。
        for (Long articleId : articleIds) {
            notificationCacheService.clearArticleUnreadLike(articleId);
        }
    }

    @Override
    public void delete(Long id, Long userId) {
        Notification n = notificationDao.selectById(id);
        if (n == null || !n.getUserId().equals(userId)) return;
        int rows = notificationDao.logicDelete(id, userId);
        if (rows > 0
                && n.getIsRead() != null && n.getIsRead() == 0
                && n.getType() != null && n.getType() == 1) {
            notificationCacheService.removeUnreadLike(
                    n.getUserId(), n.getTargetId(), n.getId());
        }
    }
}
