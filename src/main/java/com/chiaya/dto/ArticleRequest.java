package com.chiaya.dto;

import lombok.Data;

import java.util.List;

/**
 * 文章新建 / 编辑请求体。
 */
@Data
public class ArticleRequest {

    /** 所属分类ID，可为空 */
    private Long categoryId;

    /** 标签ID列表，可为空 */
    private List<Long> tagIds;

    /** 文章标题，必填，最长 200 字 */
    private String title;

    /** 文章摘要，可为空，默认截取正文前 100 字 */
    private String summary;

    /** 文章正文，必填 */
    private String content;

    /** 附加内容，可为空（仅订阅者及以上权限用户可在文章页面看到） */
    private String extraContent;

    /** 附加内容查看权限：1仅管理员，2作者及以上，3订阅者及以上，为空默认订阅者及以上 */
    private Integer extraViewLevel;

    /** 附加内容附件对应的媒体库文件ID列表，可为空 */
    private List<Long> extraFileIds;

    /** 封面图片地址（媒体库地址），可为空 */
    private String coverImage;

    /** 文章状态：0草稿，1待审核，2已发布，为空默认已发布 */
    private Integer status;

    /** 是否允许评论：为空时默认允许 */
    private Boolean allowComment;
}
