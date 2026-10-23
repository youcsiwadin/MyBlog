package com.chiaya.common;

import lombok.Getter;

/**
 * 业务异常，由全局异常处理器转换为统一响应结构。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码，同时作为 HTTP 状态码返回 */
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }
}
