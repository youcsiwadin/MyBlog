package com.chiaya.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeNotificationDetailVO {
    private Long notificationId;     // 通知ID，用于标记已读
    private Long senderId;           // 点赞人ID
    private String senderName;       // 点赞人昵称
    private String senderAvatar;     // 点赞人头像
    private Long articleId;          // 被点赞文章ID
    private String articleTitle;     // 被点赞文章标题
    private Integer articleLikeCount;// 该文章当前的总点赞数（非数据库新字段）
    private LocalDateTime createTime;// 点赞时间
    private Integer isRead;          // 0未读 1已读（明细默认查未读，恒为0）
}
