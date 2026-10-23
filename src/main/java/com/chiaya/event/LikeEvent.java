package com.chiaya.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent implements Serializable {
    private Long articleId;
    private Long authorId;
    private Long senderId;
    private String senderName;
    private String articleTitle;
    private Long timestamp;
}
