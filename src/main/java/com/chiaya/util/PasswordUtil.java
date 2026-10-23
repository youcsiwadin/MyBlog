package com.chiaya.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码摘要工具：存储格式为 Base64(salt)$SHA256(salt + password)。
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }

    /**
     * 生成带随机盐的密码摘要。
     */
    public static String encode(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + "$" + sha256Hex(salt, rawPassword);
    }

    /**
     * 校验明文密码与数据库中的摘要是否匹配。
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        int index = storedPassword.indexOf('$');
        if (index <= 0) {
            return false;
        }
        byte[] salt;
        try {
            salt = Base64.getDecoder().decode(storedPassword.substring(0, index));
        } catch (IllegalArgumentException ex) {
            return false;
        }
        String expected = storedPassword.substring(index + 1);
        byte[] actualBytes = sha256Hex(salt, rawPassword).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(actualBytes, expected.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(byte[] salt, String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                builder.append(Character.forDigit((b >> 4) & 0xF, 16));
                builder.append(Character.forDigit(b & 0xF, 16));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("当前 JVM 不支持 SHA-256", ex);
        }
    }
}
