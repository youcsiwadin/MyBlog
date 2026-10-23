-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: localhost    Database: blog
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '文章主键ID',
  `user_id` bigint unsigned NOT NULL COMMENT '作者用户ID（关联 user.id）',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '所属分类ID（关联 category.id，可为空）',
  `target_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'category' COMMENT '发布于类型：category分类，page页面',
  `target_id` bigint unsigned DEFAULT NULL COMMENT '发布于分类ID或页面ID',
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章标题',
  `summary` varchar(500) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文章摘要',
  `content` longtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章正文（Markdown 文本）',
  `extra_content` longtext COLLATE utf8mb4_general_ci COMMENT '附加内容（仅订阅者及以上权限用户可见）',
  `extra_view_level` tinyint NOT NULL DEFAULT '3' COMMENT '附加内容查看权限：1仅管理员，2作者及以上，3订阅者及以上',
  `cover_image` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图片地址（取自媒体库）',
  `status` tinyint NOT NULL DEFAULT '2' COMMENT '文章状态：0草稿，1待审核，2已发布',
  `allow_comment` tinyint NOT NULL DEFAULT '1' COMMENT '是否允许评论：1允许，0不允许',
  `view_count` int unsigned NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_user_id` (`user_id`),
  KEY `idx_article_create_time` (`create_time`),
  KEY `idx_article_category_id` (`category_id`),
  KEY `idx_article_status` (`status`),
  KEY `idx_article_target` (`target_type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_extra_file`
--

DROP TABLE IF EXISTS `article_extra_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_extra_file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint unsigned NOT NULL COMMENT '文章ID（关联 article.id）',
  `media_id` bigint unsigned NOT NULL COMMENT '媒体库文件ID（关联 media.id）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_extra_file` (`article_id`,`media_id`),
  KEY `idx_article_extra_file_article_id` (`article_id`),
  KEY `idx_article_extra_file_media_id` (`media_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章附加内容附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_like`
--

DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `author_id` bigint NOT NULL COMMENT '被点赞文章作者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`,`user_id`),
  KEY `idx_author` (`author_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint unsigned NOT NULL COMMENT '文章ID（关联 article.id）',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID（关联 tag.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`,`tag_id`),
  KEY `idx_article_tag_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类主键ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称（唯一）',
  `slug` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类别名（用于地址）',
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`),
  KEY `idx_category_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章分类目录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评论主键ID',
  `article_id` bigint unsigned NOT NULL COMMENT '所属文章ID（关联 article.id）',
  `user_id` bigint unsigned DEFAULT NULL COMMENT '评论者登录用户ID（为空表示游客评论）',
  `nickname` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论人昵称',
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评论人邮箱',
  `content` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '评论状态：0待审核，1已通过，2已驳回',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `delete_time` datetime DEFAULT NULL COMMENT '移入回收站时间（回收站保留 1 天，超时自动彻底删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_article_id` (`article_id`),
  KEY `idx_comment_status` (`status`),
  KEY `idx_comment_user_id` (`user_id`),
  KEY `idx_comment_delete_time` (`delete_time`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guest_message`
--

DROP TABLE IF EXISTS `guest_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guest_message` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '留言主键ID',
  `nickname` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '留言人昵称',
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '留言人邮箱',
  `content` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '留言内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '留言状态：1已通过，0待审核',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_guest_message_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客留言板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '媒体主键ID',
  `file_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始文件名',
  `url` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问地址（/uploads/...）',
  `size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '文件大小（字节）',
  `mime_type` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件类型',
  `uploader_id` bigint unsigned DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_media_uploader_id` (`uploader_id`),
  KEY `idx_media_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='媒体库（图片）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu_item`
--

DROP TABLE IF EXISTS `menu_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单项类型：category分类，page页面',
  `target_id` bigint unsigned NOT NULL COMMENT '分类ID或页面ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前（对应前台导航从左到右）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_item_target` (`target_type`,`target_id`),
  KEY `idx_menu_item_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客前台导航菜单项';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `moment`
--

DROP TABLE IF EXISTS `moment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `moment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '说说主键ID',
  `user_id` bigint unsigned NOT NULL COMMENT '发布用户ID（关联 user.id）',
  `content` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '说说内容',
  `status` tinyint NOT NULL DEFAULT '2' COMMENT '说说状态：0草稿，1待审核，2已发布',
  `cover_image` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面图片地址',
  `summary` varchar(500) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '说说摘要',
  `target_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'page' COMMENT '发布目标类型，固定为 page',
  `target_id` bigint unsigned DEFAULT NULL COMMENT '发布目标页面ID（说说页面）',
  `allow_comment` tinyint NOT NULL DEFAULT '1' COMMENT '是否允许评论：1允许，0不允许',
  `extra_content` longtext COLLATE utf8mb4_general_ci COMMENT '附加内容',
  `extra_view_level` tinyint NOT NULL DEFAULT '3' COMMENT '附加内容查看权限：1仅管理员，2作者及以上，3订阅者及以上，4游客及以上',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_moment_user_id` (`user_id`),
  KEY `idx_moment_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='说说表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `moment_extra_file`
--

DROP TABLE IF EXISTS `moment_extra_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `moment_extra_file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `moment_id` bigint unsigned NOT NULL COMMENT '说说ID（关联 moment.id）',
  `media_id` bigint unsigned NOT NULL COMMENT '媒体库文件ID（关联 media.id）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_moment_extra_file` (`moment_id`,`media_id`),
  KEY `idx_moment_extra_file_moment_id` (`moment_id`),
  KEY `idx_moment_extra_file_media_id` (`media_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='说说附加内容附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者用户ID',
  `type` tinyint NOT NULL COMMENT '通知类型：1点赞 2评论 3关注 4系统',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID（文章ID/评论ID等）',
  `target_type` tinyint DEFAULT NULL COMMENT '目标类型：1文章 2评论 3用户',
  `title` varchar(200) DEFAULT NULL COMMENT '通知标题',
  `content` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0未读 1已读',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0正常 1删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notify_like` (`user_id`,`sender_id`,`target_id`,`type`,`target_type`),
  KEY `idx_user_read` (`user_id`,`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `page`
--

DROP TABLE IF EXISTS `page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `page` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '页面主键ID',
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '页面标题',
  `slug` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '页面别名（唯一，可为空）',
  `page_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'normal' COMMENT '页面类型：normal普通页面，moments说说，guestbook留言板',
  `content` longtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '页面内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '页面状态：1已发布，0草稿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_slug` (`slug`),
  KEY `idx_page_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='独立页面表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `site_setting`
--

DROP TABLE IF EXISTS `site_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site_setting` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `setting_key` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项键名（唯一）',
  `setting_value` varchar(500) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置项值',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_setting_key` (`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=935 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站点主题设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '标签主键ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称（唯一）',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户名（唯一）',
  `password` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码摘要，格式：Base64(salt)$SHA256(salt+password)',
  `nickname` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户邮箱',
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '用户状态：1正常，0禁用',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT '角色：1管理员，2作者，3订阅者',
  `blogger_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '博主名字（博客展示用的署名，可空）',
  `blogger_avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '博主头像（博客展示用，可空）',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0未删除，1已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增用户ID',
  `login` varchar(50) NOT NULL COMMENT '登录用户名，不可修改',
  `pass` varchar(255) NOT NULL COMMENT '加密后的密码',
  `nicename` varchar(100) DEFAULT '' COMMENT 'URL别名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `registered` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `status` tinyint DEFAULT '0' COMMENT '登录状态：0未登录，1已登录',
  `displayname` varchar(100) DEFAULT '' COMMENT '展示昵称',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `uk_login` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-23 21:21:41
