package com.chiaya.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ARTICLE_NOT_FOUND(40001, "文章不存在"),
    CANNOT_LIKE_SELF(40002, "不能给自己点赞"),
    ALREADY_LIKED(40901, "重复点赞"),
    NOT_LIKED(40902, "尚未点赞"),
    SYSTEM_ERROR(50000, "系统繁忙，请稍后再试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
