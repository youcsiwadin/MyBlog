package com.chiaya.service.impl;

import com.chiaya.common.BusinessException;
import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.CategoryDao;
import com.chiaya.dto.CategoryRequest;
import com.chiaya.pojo.Category;
import com.chiaya.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类目录业务实现。
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final int NAME_MAX_LENGTH = 50;

    private static final int SLUG_MAX_LENGTH = 100;

    private static final int DESCRIPTION_MAX_LENGTH = 255;

    private final CategoryDao categoryDao;

    private final ArticleDao articleDao;

    public CategoryServiceImpl(CategoryDao categoryDao, ArticleDao articleDao) {
        this.categoryDao = categoryDao;
        this.articleDao = articleDao;
    }

    @Override
    public List<Category> list() {
        return categoryDao.listAll();
    }

    @Override
    public Category create(CategoryRequest request) {
        String name = validateName(request.getName());
        if (categoryDao.findByName(name) != null) {
            throw BusinessException.badRequest("分类名称已存在");
        }
        Category category = new Category();
        category.setName(name);
        category.setSlug(validateSlug(request.getSlug()));
        category.setDescription(validateDescription(request.getDescription()));
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryDao.insert(category);
        return categoryDao.findById(category.getId());
    }

    @Override
    public Category update(Long id, CategoryRequest request) {
        Category existing = categoryDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("分类不存在或已被删除");
        }
        String name = validateName(request.getName());
        Category sameName = categoryDao.findByName(name);
        if (sameName != null && !sameName.getId().equals(id)) {
            throw BusinessException.badRequest("分类名称已存在");
        }
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(validateSlug(request.getSlug()));
        category.setDescription(validateDescription(request.getDescription()));
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryDao.update(category);
        return categoryDao.findById(id);
    }

    @Override
    public void delete(Long id) {
        Category existing = categoryDao.findById(id);
        if (existing == null) {
            throw BusinessException.notFound("分类不存在或已被删除");
        }
        long articleCount = articleDao.countByCategoryId(id);
        if (articleCount > 0) {
            throw BusinessException.badRequest("该分类下还有 " + articleCount + " 篇文章，请先调整文章分类后再删除");
        }
        categoryDao.softDelete(id);
    }

    private String validateName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw BusinessException.badRequest("分类名称不能为空");
        }
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw BusinessException.badRequest("分类名称不能超过 50 个字符");
        }
        return trimmed;
    }

    private String validateSlug(String slug) {
        String trimmed = slug == null ? "" : slug.trim();
        if (trimmed.length() > SLUG_MAX_LENGTH) {
            throw BusinessException.badRequest("分类别名不能超过 100 个字符");
        }
        return trimmed;
    }

    private String validateDescription(String description) {
        String trimmed = description == null ? "" : description.trim();
        if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw BusinessException.badRequest("分类描述不能超过 255 个字符");
        }
        return trimmed;
    }
}
