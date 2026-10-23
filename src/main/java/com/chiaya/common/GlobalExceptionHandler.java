package com.chiaya.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：保证任何异常都返回统一响应结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException ex) {
        Integer code = ex.getCode() == null ? 400 : ex.getCode();
        HttpStatus status = HttpStatus.resolve(code) == null ? HttpStatus.BAD_REQUEST : HttpStatus.valueOf(code);
        return ResponseEntity.status(status).body(Result.error(code, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "请求参数不合法"));
    }

    /**
     * 上传文件超过大小限制：返回明确的提示，而不是 500 服务器错误。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "图片过大，请压缩后重试（单个图片最大 40MB）"));
    }

    /**
     * 其他文件上传异常（例如请求不是 multipart、上传被中断等）。
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Result<Void>> handleMultipartException(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, "文件上传失败，请确认已选择正确的图片文件"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        // 部分容器会以 SizeLimitExceededException 的形式抛出上传超限异常
        if (isUploadSizeExceeded(ex)) {
            log.warn("上传内容超过大小限制：{}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "图片过大，请压缩后重试（单个图片最大 40MB）"));
        }
        log.error("服务器内部错误", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误，请稍后重试"));
    }

    /**
     * 判断异常链中是否包含上传大小超限异常。
     */
    private boolean isUploadSizeExceeded(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String name = current.getClass().getName();
            if (name.contains("SizeLimitExceededException") || name.contains("MaxUploadSizeExceededException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
