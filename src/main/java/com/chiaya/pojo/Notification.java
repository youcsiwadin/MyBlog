package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private Long senderId;
    private Integer type;
    private Long targetId;
    private Integer targetType;
    private String title;
    private String content;
    private Integer isRead;
    private Integer deleted;
    private LocalDateTime createTime;
}
