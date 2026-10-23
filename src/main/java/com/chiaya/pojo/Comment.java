package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体，对应数据表 comment。
 */
@Data
public class Comment {

    private Long id;

    private Long articleId;

    /** 评论者登录用户ID（历史游客评论为空；新评论必须登录） */
    private Long userId;

    private String nickname;

    private String email;

    private String content;

    /** 状态：0待审核，1已通过，2已驳回 */
    private Integer status;

    private Integer deleted;

    /** 移入回收站的时间 */
    private LocalDateTime deleteTime;

    /** 距离自动彻底删除的剩余秒数（统计字段，非数据库列） */
    private Long remainingSeconds;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 所属文章标题（联表查询字段，非数据库列） */
    private String articleTitle;

    /** 评论者登录账号昵称（联表查询字段，非数据库列） */
    private String userName;
}
