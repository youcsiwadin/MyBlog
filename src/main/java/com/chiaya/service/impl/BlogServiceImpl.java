package com.chiaya.service.impl;

import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.CategoryDao;
import com.chiaya.dao.TagDao;
import com.chiaya.dao.UserDao;
import com.chiaya.common.BusinessException;
import com.chiaya.pojo.User;
import com.chiaya.service.BlogService;
import com.chiaya.service.SiteSettingService;
import com.chiaya.vo.BlogSidebarVO;
import com.chiaya.vo.BlogAuthorVO;
import com.chiaya.vo.SiteSettingVO;
import com.chiaya.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客站点业务实现。
 */
@Service
public class BlogServiceImpl implements BlogService {

    private final ArticleDao articleDao;

    private final CategoryDao categoryDao;

    private final TagDao tagDao;

    private final SiteSettingService siteSettingService;

    private final UserDao userDao;

    public BlogServiceImpl(ArticleDao articleDao, CategoryDao categoryDao, TagDao tagDao,
                           SiteSettingService siteSettingService, UserDao userDao) {
        this.articleDao = articleDao;
        this.categoryDao = categoryDao;
        this.tagDao = tagDao;
        this.siteSettingService = siteSettingService;
        this.userDao = userDao;
    }

    @Override
    public BlogAuthorVO author(Long userId) {
        if (userId == null) {
            throw BusinessException.badRequest("缺少作者ID");
        }
        User user = userDao.findById(userId);
        if (user == null) {
            throw BusinessException.notFound("作者不存在");
        }
        BlogAuthorVO vo = new BlogAuthorVO();
        vo.setId(user.getId());
        String name = user.getBloggerName() == null || user.getBloggerName().isBlank()
                ? user.getNickname() : user.getBloggerName();
        vo.setName(name == null || name.isBlank() ? user.getUsername() : name);
        vo.setAvatar(user.getBloggerAvatar() == null || user.getBloggerAvatar().isBlank()
                ? user.getAvatar() : user.getBloggerAvatar());
        vo.setArticleCount(articleDao.countPublishedByAuthor(userId));
        return vo;
    }

    @Override
    public BlogSidebarVO sidebar() {
        SiteSettingVO settings = siteSettingService.getSettings();
        BlogSidebarVO vo = new BlogSidebarVO();
        vo.setAuthorName(settings.getAuthorName());
        vo.setAuthorAvatar(settings.getAuthorAvatar());
        vo.setArticleCount(articleDao.countAll());
        vo.setCategoryCount(categoryDao.countAll());
        vo.setTagCount(tagDao.countAll());

        List<TagVO> categories = new ArrayList<>();
        categoryDao.listAll().forEach(category -> categories.add(new TagVO(category.getId(), category.getName())));
        vo.setCategories(categories);

        List<TagVO> tags = new ArrayList<>();
        tagDao.listAll().forEach(tag -> tags.add(new TagVO(tag.getId(), tag.getName())));
        vo.setTags(tags);
        return vo;
    }
}
