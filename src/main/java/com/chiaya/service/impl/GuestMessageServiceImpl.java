package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.dao.GuestMessageDao;
import com.chiaya.dto.GuestMessageRequest;
import com.chiaya.pojo.GuestMessage;
import com.chiaya.service.GuestMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 留言板业务实现。
 */
@Service
public class GuestMessageServiceImpl implements GuestMessageService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final int NICKNAME_MAX_LENGTH = 50;

    private static final int EMAIL_MAX_LENGTH = 100;

    private static final int CONTENT_MAX_LENGTH = 1000;

    private final GuestMessageDao guestMessageDao;

    public GuestMessageServiceImpl(GuestMessageDao guestMessageDao) {
        this.guestMessageDao = guestMessageDao;
    }

    @Override
    public PageResult<GuestMessage> page(Integer page, Integer pageSize) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        long total = guestMessageDao.countAll();
        List<GuestMessage> records = total == 0
                ? List.of()
                : guestMessageDao.pageList((currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public Long create(GuestMessageRequest request) {
        GuestMessage message = new GuestMessage();
        message.setNickname(validateRequired(request.getNickname(), "昵称", NICKNAME_MAX_LENGTH));
        message.setEmail(validateOptional(request.getEmail(), "邮箱", EMAIL_MAX_LENGTH));
        message.setContent(validateRequired(request.getContent(), "留言内容", CONTENT_MAX_LENGTH));
        // 留言板留言默认直接展示
        message.setStatus(1);
        guestMessageDao.insert(message);
        return message.getId();
    }

    private String validateRequired(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest(fieldName + "不能为空");
        }
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }

    private String validateOptional(String value, String fieldName, int maxLength) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.length() > maxLength) {
            throw BusinessException.badRequest(fieldName + "不能超过 " + maxLength + " 个字符");
        }
        return trimmed;
    }
}
