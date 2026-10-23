package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.common.PageResult;
import com.chiaya.dao.PageDao;
import com.chiaya.dto.PageRequest;
import com.chiaya.pojo.Page;
import com.chiaya.service.PageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 页面业务实现。
 */
@Service
public class PageServiceImpl implements PageService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 50;

    private static final int TITLE_MAX_LENGTH = 200;

    private static final int SLUG_MAX_LENGTH = 100;

    private final PageDao pageDao;

    public PageServiceImpl(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    @Override
    public PageResult<Page> page(Integer page, Integer pageSize, String keyword, Integer status) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        String trimmedKeyword = normalize(keyword);
        if (status != null && status != 0 && status != 1) {
            throw BusinessException.badRequest("页面状态不合法");
        }
        long total = pageDao.countByCondition(trimmedKeyword, status);
        List<Page> records = total == 0
                ? List.of()
                : pageDao.pageList(trimmedKeyword, status, (currentPage - 1) * currentSize, currentSize);
        return new PageResult<>(records, total, currentPage, currentSize);
    }

    @Override
    public Page getById(Long id) {
        Page page = pageDao.findById(id);
        if (page == null) {
            throw BusinessException.notFound("页面不存在或已被删除");
        }
        return page;
    }

    @Override
    public Page create(PageRequest request) {
        Page page = new Page();
        page.setTitle(validateTitle(request.getTitle()));
        page.setSlug(resolveSlug(request.getSlug(), null));
        page.setContent(validateContent(request.getContent()));
        page.setStatus(resolveStatus(request.getStatus()));
        pageDao.insert(page);
        return pageDao.findById(page.getId());
    }

    @Override
    public Page update(Long id, PageRequest request) {
        Page existing = pageDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("页面不存在或已被删除");
        }
        Page page = new Page();
        page.setId(id);
        page.setTitle(validateTitle(request.getTitle()));
        page.setSlug(resolveSlug(request.getSlug(), id));
        page.setContent(validateContent(request.getContent()));
        page.setStatus(resolveStatus(request.getStatus()));
        pageDao.update(page);
        return pageDao.findById(id);
    }

    @Override
    public void delete(Long id) {
        Page existing = pageDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("页面不存在或已被删除");
        }
        pageDao.softDelete(id);
    }

    private String validateTitle(String title) {
        String trimmed = title == null ? "" : title.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("页面标题不能为空");
        }
        if (trimmed.length() > TITLE_MAX_LENGTH) {
            throw BusinessException.badRequest("页面标题不能超过 200 个字符");
        }
        return trimmed;
    }

    private String validateContent(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("页面内容不能为空");
        }
        return trimmed;
    }

    private Integer resolveStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw BusinessException.badRequest("页面状态不合法");
        }
        return status;
    }

    /**
     * 页面别名：为空时存 null，非空时校验唯一性。
     */
    private String resolveSlug(String slug, Long currentId) {
        String trimmed = normalize(slug);
        if (trimmed == null) {
            return null;
        }
        if (trimmed.length() > SLUG_MAX_LENGTH) {
            throw BusinessException.badRequest("页面别名不能超过 100 个字符");
        }
        Page sameSlug = pageDao.findBySlug(trimmed);
        if (sameSlug != null && (currentId == null || !sameSlug.getId().equals(currentId))) {
            throw BusinessException.badRequest("页面别名已存在");
        }
        return trimmed;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
