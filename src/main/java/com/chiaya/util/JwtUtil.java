package com.chiaya.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 轻量 JWT 工具（HS256），仅依赖 JDK，用于登录 Token 的签发与校验。
 *
 * <p>载荷为标准 JSON：{"sub":用户ID,"exp":过期时间戳}，仅包含数字声明，
 * 因此可以在不引入 JSON 类库的情况下安全解析。</p>
 */
@Component
public class JwtUtil {

    private static final String HEADER = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    private final byte[] secret;

    private final long expireSeconds;

    public JwtUtil(@Value("${blog.jwt.secret}") String secret,
                   @Value("${blog.jwt.expire-minutes:120}") long expireMinutes) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expireSeconds = expireMinutes * 60;
    }

    /**
     * 签发 Token。
     */
    public String createToken(Long userId) {
        long expireAt = System.currentTimeMillis() / 1000 + expireSeconds;
        String json = "{\"sub\":" + userId + ",\"exp\":" + expireAt + "}";
        String payloadSegment = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String content = HEADER + "." + payloadSegment;
        return content + "." + sign(content);
    }

    /**
     * 解析 Token 中的用户 ID。
     *
     * @return 校验通过返回用户 ID，否则返回 null
     */
    public Long parseUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        String content = parts[0] + "." + parts[1];
        if (!MessageDigest.isEqual(sign(content).getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            return null;
        }
        try {
            String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Long expireAt = readNumber(json, "exp");
            if (expireAt == null || expireAt < System.currentTimeMillis() / 1000) {
                return null;
            }
            return readNumber(json, "sub");
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * 从 JSON 文本中读取数字声明的值。
     */
    private Long readNumber(String json, String key) {
        String prefix = "\"" + key + "\":";
        int start = json.indexOf(prefix);
        if (start < 0) {
            return null;
        }
        int index = start + prefix.length();
        int end = index;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        if (end == index) {
            return null;
        }
        try {
            return Long.valueOf(json.substring(index, end));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Token 签名失败", ex);
        }
    }
}
