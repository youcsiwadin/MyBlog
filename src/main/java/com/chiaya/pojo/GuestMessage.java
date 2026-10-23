package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言板留言实体，对应数据表 guest_message。
 */
@Data
public class GuestMessage {

    private Long id;

    private String nickname;

    private String email;

    private String content;

    /** 状态：1已通过，0待审核 */
    private Integer status;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
