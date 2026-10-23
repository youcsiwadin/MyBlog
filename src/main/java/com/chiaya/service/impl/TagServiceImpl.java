package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.dao.TagDao;
import com.chiaya.dto.TagRequest;
import com.chiaya.pojo.Tag;
import com.chiaya.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签业务实现。
 */
@Service
public class TagServiceImpl implements TagService {

    private static final int NAME_MAX_LENGTH = 50;

    private final TagDao tagDao;

    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<Tag> list() {
        return tagDao.listAll();
    }

    @Override
    public Tag create(TagRequest request) {
        String name = validateName(request.getName());
        if (tagDao.findByName(name) != null) {
            throw BusinessException.badRequest("标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tagDao.insert(tag);
        return tagDao.findById(tag.getId());
    }

    @Override
    public Tag update(Long id, TagRequest request) {
        Tag existing = tagDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("标签不存在或已被删除");
        }
        String name = validateName(request.getName());
        Tag sameName = tagDao.findByName(name);
        if (sameName != null && !sameName.getId().equals(id)) {
            throw BusinessException.badRequest("标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tagDao.update(tag);
        return tagDao.findById(id);
    }

    @Override
    public void delete(Long id) {
        Tag existing = tagDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("标签不存在或已被删除");
        }
        tagDao.softDelete(id);
    }

    private String validateName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("标签名称不能为空");
        }
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw BusinessException.badRequest("标签名称不能超过 50 个字符");
        }
        return trimmed;
    }
}
