# 前言
MyBlog是一个博客内容管理系统，参考wordpress，实现部分功能。
这是一个Vue＋Spring Boot的Java全栈项目，基于RBAC模型做权限分级，动态配置菜单，博客前台前端页面实现页面元素级别的权限控制。注册和找回密码通过邮箱验证码进行验证。通过WebSocket实现站内信功能，以实现实时的点赞通知功能。
密码加盐存储＋SHA-256，JWT权限认证，实现登录的数据安全。

# 源代码
前端代码：[前端项目GitHub地址](https://github.com/youcsiwadin/vue_blog.git)  
后端代码：[后端项目Github地址](https://github.com/youcsiwadin/MyBlog.git)

# 说明
.sql文件脚本放在sql文件夹中，导入即可创建数据库  
前端的运行：先运行npm install安装生成环境到本地，再运行npm dev run即可启动前端项目  
管理员权限用户的创建：当目前没有管理员账户时，注册页面可跳转到管理员注册页面，输入相应信息即可注册

# 技术栈
前端：Vue3＋TS＋Element-plus＋Axios  
后端：Spring Boot＋Maven＋JWT＋MySQL＋Redis＋Socket＋Swagger

# 目标功能
登录、注册  ✔  
找回密码    ✔  
动态首页    ✔  
个人设置    ✔  
用户管理    ✔  
菜单管理    ✔  
文章发布    ✔  
文章管理    ✔  
附件添加    ✔  
评论管理    ✔  
回收站      ✔  
站内信      ✔  
消息提醒    ✔  
权限控制    ✔
