package com.chiaya.dao;

import com.chiaya.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层，SQL 定义在 resources/mapper/UserMapper.xml。
 */
@Mapper
public interface UserDao {

    /**
     * 根据用户名查询用户。
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据主键查询用户。
     */
    User findById(@Param("id") Long id);

    /**
     * 新增用户，回填自增主键。
     */
    int insert(User user);

    /**
     * 后台用户列表分页查询。
     */
    List<User> pageList(@Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    /**
     * 统计符合条件的用户数量。
     */
    long countByKeyword(@Param("keyword") String keyword);

    /**
     * 更新用户状态（启用 / 禁用）。
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新用户角色。
     */
    int updateRole(@Param("id") Long id, @Param("role") Integer role);

    /**
     * 逻辑删除用户。
     */
    int softDelete(@Param("id") Long id);

    /**
     * 更新个人资料。
     */
    int updateProfile(User user);

    /**
     * 更新密码。
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新用户自己的博主名字与头像。
     */
    int updateBlogger(@Param("id") Long id,
                      @Param("bloggerName") String bloggerName,
                      @Param("bloggerAvatar") String bloggerAvatar);

    long countAll();

    /**
     * 统计当前启用中的管理员数量。
     */
    long countActiveAdmins();

    /**
     * 点赞用户获取
     */
    User selectById(@Param("id") Long id);
}
