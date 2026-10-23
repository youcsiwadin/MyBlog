package com.chiaya.service;

import com.chiaya.vo.LikeNotificationDetailVO;

import java.util.List;

public interface NotificationCacheService {

    // ========== 写入（消费者调用） ==========
    /** 新增一条未读点赞：计数 +1，明细写入 Hash */
    void addUnreadLike(Long authorId, Long articleId, LikeNotificationDetailVO detail);

    // ========== 移除（已读/删除/取消点赞调用） ==========
    /** 移除一条未读点赞：计数 -1，明细从 Hash 删 */
    void removeUnreadLike(Long authorId, Long articleId, Long notificationId);

    /** 作者全部已读：计数清零，明细全删 */
    void clearAuthorUnreadLike(Long authorId);

    /** 文章全部已读：计数清零，明细全删 */
    void clearArticleUnreadLike(Long articleId);

    // ========== 查询 ==========
    /** 某篇文章未读点赞数（Redis miss 时从 DB 重建） */
    long getArticleUnreadLike(Long articleId);

    /** 某作者总未读点赞数（Redis miss 时从 DB 重建） */
    long getAuthorUnreadLike(Long authorId);

    /** 某篇文章未读点赞明细（点赞人昵称 + 文章标题） */
    List<LikeNotificationDetailVO> getArticleUnreadLikeDetails(Long articleId);

    /** 某作者未读点赞明细（按时间倒序） */
    List<LikeNotificationDetailVO> getAuthorUnreadLikeDetails(Long authorId);
}
