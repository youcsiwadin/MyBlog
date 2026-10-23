package com.chiaya.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Integer type;
    private Long targetId;
    private Integer targetType;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
