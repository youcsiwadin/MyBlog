package com.chiaya.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long articleId;
    private Long authorId;
    private Long senderId;
    private String senderName;
    private String articleTitle;
    private Long timestamp;
}
