package com.chiaya.service;


import com.chiaya.common.PageResult;
import com.chiaya.pojo.Notification;
import com.chiaya.vo.NotificationVO;

public interface NotificationService {

    PageResult<NotificationVO> page(Long userId, Integer page, Integer pageSize,
                                    Integer type, Integer isRead);

    long countUnread(Long userId);

    void markRead(Long id, Long userId);

    void markAllRead(Long userId);

    void delete(Long id, Long userId);

    /**
     * 保存通知（供 MQ 消费者调用）
     */
    void saveNotification(Notification notification);
}
