package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.dao.MomentDao;
import com.chiaya.dto.MomentRequest;
import com.chiaya.pojo.Moment;
import com.chiaya.service.MomentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说说业务实现。
 */
@Service
public class MomentServiceImpl implements MomentService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final int CONTENT_MAX_LENGTH = 1000;

    private final MomentDao momentDao;

    public MomentServiceImpl(MomentDao momentDao) {
        this.momentDao = momentDao;
    }

    @Override
    public PageResult<Moment> page(Integer page, Integer pageSize) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        long total = momentDao.countAll();
        List<Moment> records = total == 0
                ? List.of()
                : momentDao.pageList((currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public Long create(Long userId, MomentRequest request) {
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (content.isEmpty()) {
            throw BusinessException.badRequest("说说内容不能为空");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw BusinessException.badRequest("说说内容不能超过 1000 个字符");
        }
        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setContent(content);
        momentDao.insert(moment);
        return moment.getId();
    }

    @Override
    public void delete(Long userId, Long id) {
        Moment existing = momentDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("说说不存在或已被删除");
        }
        if (!existing.getUserId().equals(userId)) {
            throw BusinessException.forbidden("只能删除自己发表的说说");
        }
        momentDao.softDelete(id, userId);
    }
}
