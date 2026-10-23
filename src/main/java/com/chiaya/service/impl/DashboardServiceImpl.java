package com.chiaya.service.impl;

import com.chiaya.dao.ArticleDao;
import com.chiaya.dao.CategoryDao;
import com.chiaya.dao.CommentDao;
import com.chiaya.dao.MomentDao;
import com.chiaya.dao.PageDao;
import com.chiaya.dao.TagDao;
import com.chiaya.dao.UserDao;
import com.chiaya.service.DashboardService;
import com.chiaya.vo.DashboardSummaryVO;
import org.springframework.stereotype.Service;

/**
 * 首页仪表盘业务实现。
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int RECENT_LIMIT = 5;

    private final ArticleDao articleDao;

    private final CategoryDao categoryDao;

    private final TagDao tagDao;

    private final PageDao pageDao;

    private final CommentDao commentDao;

    private final MomentDao momentDao;

    private final UserDao userDao;

    public DashboardServiceImpl(ArticleDao articleDao, CategoryDao categoryDao, TagDao tagDao, PageDao pageDao,
                                CommentDao commentDao, MomentDao momentDao, UserDao userDao) {
        this.articleDao = articleDao;
        this.categoryDao = categoryDao;
        this.tagDao = tagDao;
        this.pageDao = pageDao;
        this.commentDao = commentDao;
        this.momentDao = momentDao;
        this.userDao = userDao;
    }

    @Override
    public DashboardSummaryVO summary() {
        DashboardSummaryVO vo = new DashboardSummaryVO();
        vo.setArticleCount(articleDao.countAll());
        vo.setCategoryCount(categoryDao.countAll());
        vo.setTagCount(tagDao.countAll());
        vo.setPageCount(pageDao.countAll());
        vo.setCommentCount(commentDao.countAll());
        vo.setPendingCommentCount(commentDao.countByStatus(0));
        vo.setMomentCount(momentDao.countAll());
        vo.setUserCount(userDao.countAll());
        vo.setRecentArticles(articleDao.recentList(RECENT_LIMIT));
        vo.setRecentComments(commentDao.recentList(RECENT_LIMIT));
        return vo;
    }
}
