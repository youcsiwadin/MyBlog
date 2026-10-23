package com.chiaya.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体，对应数据表 article。
 */
@Data
public class Article {

    /** 文章主键ID */
    private Long id;

    /** 作者用户ID */
    private Long userId;

    /** 所属分类ID */
    private Long categoryId;

    /** 文章标题 */
    private String title;

    /** 文章摘要 */
    private String summary;

    /** 文章正文 */
    private String content;

    /** 附加内容（仅订阅者及以上权限用户可在文章页面看到） */
    private String extraContent;

    /** 附加内容查看权限：1仅管理员，2作者及以上，3订阅者及以上 */
    private Integer extraViewLevel;

    /** 封面图片地址（取自媒体库） */
    private String coverImage;

    /** 文章状态：0草稿，1待审核，2已发布 */
    private Integer status;

    /** 是否允许评论：1允许，0不允许 */
    private Integer allowComment;

    /** 浏览量 */
    private Integer viewCount;


    /** 点赞数 */
    private Integer likeCount;

    /** 文章字数（统计字段，非数据库列） */
    private Integer wordCount;

    /** 逻辑删除标记 */
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 作者昵称（联表查询字段，非数据库列） */
    private String authorName;

    /** 作者头像（联表查询字段，非数据库列） */
    private String authorAvatar;

    /** 该作者已发布文章数（统计字段，非数据库列） */
    private Long authorArticleCount;

    /** 分类名称（联表查询字段，非数据库列） */
    private String categoryName;

    /** 标签列表（联表查询字段，非数据库列） */
    private java.util.List<com.chiaya.vo.TagVO> tags;

    /** 附加内容附件列表（联表查询字段，非数据库列，仅有权限时返回） */
    private java.util.List<com.chiaya.vo.ExtraFileVO> extraFiles;

    /** 该文章存在附加内容但当前用户权限不足（非数据库列） */
    private Boolean extraLocked;

    /** 当前登录用户是否已点赞（非数据库列，未登录恒为 false） */
    private Boolean liked;


}
