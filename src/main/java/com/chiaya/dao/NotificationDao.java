package com.chiaya.dao;

import com.chiaya.pojo.Notification;
import com.chiaya.vo.LikeNotificationDetailVO;
import com.chiaya.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationDao {
    int insert(Notification notification);
    List<NotificationVO> pageByUserId(@Param("userId") Long userId,
                                      @Param("type") Integer type,
                                      @Param("isRead") Integer isRead,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);
    long countByUserId(@Param("userId") Long userId,
                       @Param("type") Integer type,
                       @Param("isRead") Integer isRead);
    long countUnread(@Param("userId") Long userId);
    int markRead(@Param("id") Long id, @Param("userId") Long userId);
    int markAllRead(@Param("userId") Long userId);
    int logicDelete(@Param("id") Long id, @Param("userId") Long userId);
    Notification selectById(@Param("id") Long id);

    // ===== 未读点赞计数（兜底用） =====
    long countUnreadLikeByArticle(@Param("articleId") Long articleId);
    long countUnreadLikeByAuthor(@Param("authorId") Long authorId);

    // ===== 未读点赞明细（兜底回填用） =====
    List<LikeNotificationDetailVO> listUnreadLikeDetailsByArticle(@Param("articleId") Long articleId);
    List<LikeNotificationDetailVO> listUnreadLikeDetailsByAuthor(@Param("authorId") Long authorId);

    // ===== 取消点赞时查/删点赞通知 =====
    Notification selectLikeNotification(@Param("authorId") Long authorId,
                                        @Param("senderId") Long senderId,
                                        @Param("articleId") Long articleId);
    int deleteLikeNotification(@Param("authorId") Long authorId,
                               @Param("senderId") Long senderId,
                               @Param("articleId") Long articleId);

}
