/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : localhost:3306
 Source Schema         : website

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 28/06/2026 14:12:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_login_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_login_log`;
CREATE TABLE `admin_login_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(0) NULL DEFAULT NULL,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `login_region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地区(高德IP定位)',
  `abnormal` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否异常登录(国外IP):1是0否',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `login_type` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1登录 2登出',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1成功 0失败',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `login_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin`(`admin_id`) USING BTREE,
  INDEX `idx_time`(`login_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台登录日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_login_log
-- ----------------------------
INSERT INTO `admin_login_log` VALUES (58, 1, 'admin', '183.254.112.65', '海南省海口市', 0, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-28 14:12:03');

-- ----------------------------
-- Table structure for admin_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_operation_log`;
CREATE TABLE `admin_operation_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(0) NULL DEFAULT NULL,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operation` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类.方法',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GET/POST...',
  `request_param` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `response_result` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作地区(高德IP定位)',
  `abnormal` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否异常(国外IP):1是0否',
  `cost_ms` bigint(0) NULL DEFAULT NULL COMMENT '耗时(毫秒)',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1成功 0异常',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin`(`admin_id`) USING BTREE,
  INDEX `idx_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for admin_permission
-- ----------------------------
DROP TABLE IF EXISTS `admin_permission`;
CREATE TABLE `admin_permission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限码 user:list 等',
  `permission_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `permission_type` tinyint(0) NOT NULL COMMENT '1菜单 2按钮 3接口',
  `parent_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '父级ID',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由/接口路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端组件',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted` tinyint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台权限/菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_permission
-- ----------------------------
INSERT INTO `admin_permission` VALUES (1, 'dashboard:view', '主控制台', 1, 0, '/dashboard', NULL, 'DataLine', 1, 1, '2026-06-24 10:49:42', '2026-06-24 11:55:32', 0);
INSERT INTO `admin_permission` VALUES (10, 'user:menu', '用户中心', 1, 0, '/user', NULL, 'User', 2, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (11, 'user:list', '用户列表', 1, 10, '/user/list', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (12, 'user:detail', '查看详情', 2, 11, '', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (13, 'user:disable', '启用/禁用', 2, 11, '', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (14, 'user:export', '导出', 2, 11, '', NULL, '', 3, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (20, 'order:menu', '会员与订单', 1, 0, '/order', NULL, 'Goods', 3, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (21, 'order:list', '订单管理', 1, 20, '/order/list', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (22, 'order:detail', '订单详情', 2, 21, '', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (23, 'order:refund', '退款操作', 2, 21, '', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (24, 'order:export', '导出', 2, 21, '', NULL, '', 3, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (25, 'goods:list', '商品管理', 1, 20, '/order/goods', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (26, 'goods:edit', '编辑商品', 2, 25, '', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (27, 'func:config', '功能配置', 1, 20, '/order/func-config', NULL, '', 3, 1, '2026-06-24 10:49:42', '2026-06-24 11:36:08', 1);
INSERT INTO `admin_permission` VALUES (28, 'benefit:list', '权益管理', 1, 20, '/order/benefit', NULL, '', 4, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (29, 'payfail:list', '支付异常监控', 1, 20, '/order/payment-failure', NULL, '', 5, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (30, 'content:menu', '内容运营', 1, 0, '/content', NULL, 'Picture', 4, 1, '2026-06-24 10:49:42', '2026-06-24 11:43:59', 1);
INSERT INTO `admin_permission` VALUES (31, 'banner:list', 'Banner管理', 1, 30, '/content/banner', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 11:43:59', 1);
INSERT INTO `admin_permission` VALUES (32, 'notice:list', '文案/公告', 1, 30, '/content/notice', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-24 11:43:59', 1);
INSERT INTO `admin_permission` VALUES (35, 'analytics:menu', '数据分析', 1, 0, '/analytics', NULL, 'TrendCharts', 5, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (36, 'analytics:user', '用户分析', 1, 35, '/analytics/user', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-26 09:44:59', 1);
INSERT INTO `admin_permission` VALUES (37, 'analytics:income', '收入分析', 1, 35, '/analytics/income', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-26 09:44:59', 1);
INSERT INTO `admin_permission` VALUES (38, 'analytics:func', '功能分析', 1, 35, '/analytics/func', NULL, '', 3, 1, '2026-06-24 10:49:42', '2026-06-26 09:44:59', 1);
INSERT INTO `admin_permission` VALUES (39, 'analytics:realtime', '实时数据', 1, 35, '/analytics/realtime', NULL, '', 4, 1, '2026-06-26 09:35:07', '2026-06-26 09:35:07', 0);
INSERT INTO `admin_permission` VALUES (40, 'system:menu', '系统管理', 1, 0, '/system', NULL, 'Setting', 6, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (41, 'admin:list', '管理员管理', 2, 40, '/system/admin', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-27 21:25:20', 0);
INSERT INTO `admin_permission` VALUES (42, 'admin:edit', '编辑管理员', 2, 41, '', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (43, 'role:list', '成员管理', 1, 40, '/system/role', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-27 21:25:20', 0);
INSERT INTO `admin_permission` VALUES (48, 'log:login', '登录日志', 1, 40, '/system/login-log', NULL, '', 5, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (49, 'log:operation', '操作日志', 1, 40, '/system/operation-log', NULL, '', 6, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (50, 'analytics:newdata', '新增数据', 1, 35, '/analytics/newdata', NULL, '', 2, 1, '2026-06-26 21:23:33', '2026-06-26 21:23:33', 0);
INSERT INTO `admin_permission` VALUES (51, 'analytics:payanalysis', '付费分析', 1, 35, '/analytics/payment', NULL, '', 3, 1, '2026-06-26 22:31:13', '2026-06-26 22:31:13', 0);
INSERT INTO `admin_permission` VALUES (52, 'analytics:active', '活跃数据', 1, 35, '/analytics/active', NULL, '', 5, 1, '2026-06-27 16:05:55', '2026-06-27 16:05:55', 0);
INSERT INTO `admin_permission` VALUES (53, 'analytics:retention', '留存数据', 1, 35, '/analytics/retention', NULL, '', 6, 1, '2026-06-28 09:17:15', '2026-06-28 09:17:15', 0);

-- ----------------------------
-- Table structure for admin_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码 SUPER_ADMIN/OPERATOR...',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `sort_order` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted` tinyint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role
-- ----------------------------
INSERT INTO `admin_role` VALUES (1, 'SUPER_ADMIN', '超级管理员', '拥有全部权限', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_role` VALUES (7, 'MEMBER', '普通成员', '普通成员：除角色增删改外拥有全部权限', 1, 7, '2026-06-27 20:36:24', '2026-06-28 08:26:03', 0);

-- ----------------------------
-- Table structure for admin_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `admin_role_permission`;
CREATE TABLE `admin_role_permission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL,
  `permission_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id`, `permission_id`) USING BTREE,
  INDEX `idx_role`(`role_id`) USING BTREE,
  INDEX `idx_perm`(`permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role_permission
-- ----------------------------
INSERT INTO `admin_role_permission` VALUES (1, 1, 1);
INSERT INTO `admin_role_permission` VALUES (2, 1, 10);
INSERT INTO `admin_role_permission` VALUES (7, 1, 11);
INSERT INTO `admin_role_permission` VALUES (8, 1, 12);
INSERT INTO `admin_role_permission` VALUES (9, 1, 13);
INSERT INTO `admin_role_permission` VALUES (10, 1, 14);
INSERT INTO `admin_role_permission` VALUES (3, 1, 20);
INSERT INTO `admin_role_permission` VALUES (11, 1, 21);
INSERT INTO `admin_role_permission` VALUES (16, 1, 22);
INSERT INTO `admin_role_permission` VALUES (17, 1, 23);
INSERT INTO `admin_role_permission` VALUES (18, 1, 24);
INSERT INTO `admin_role_permission` VALUES (12, 1, 25);
INSERT INTO `admin_role_permission` VALUES (19, 1, 26);
INSERT INTO `admin_role_permission` VALUES (14, 1, 28);
INSERT INTO `admin_role_permission` VALUES (15, 1, 29);
INSERT INTO `admin_role_permission` VALUES (5, 1, 35);
INSERT INTO `admin_role_permission` VALUES (34, 1, 39);
INSERT INTO `admin_role_permission` VALUES (6, 1, 40);
INSERT INTO `admin_role_permission` VALUES (25, 1, 41);
INSERT INTO `admin_role_permission` VALUES (31, 1, 42);
INSERT INTO `admin_role_permission` VALUES (26, 1, 43);
INSERT INTO `admin_role_permission` VALUES (29, 1, 48);
INSERT INTO `admin_role_permission` VALUES (30, 1, 49);
INSERT INTO `admin_role_permission` VALUES (36, 1, 50);
INSERT INTO `admin_role_permission` VALUES (37, 1, 51);
INSERT INTO `admin_role_permission` VALUES (38, 1, 52);
INSERT INTO `admin_role_permission` VALUES (133, 1, 53);
INSERT INTO `admin_role_permission` VALUES (102, 7, 1);
INSERT INTO `admin_role_permission` VALUES (103, 7, 10);
INSERT INTO `admin_role_permission` VALUES (104, 7, 11);
INSERT INTO `admin_role_permission` VALUES (105, 7, 12);
INSERT INTO `admin_role_permission` VALUES (106, 7, 13);
INSERT INTO `admin_role_permission` VALUES (107, 7, 14);
INSERT INTO `admin_role_permission` VALUES (108, 7, 20);
INSERT INTO `admin_role_permission` VALUES (109, 7, 21);
INSERT INTO `admin_role_permission` VALUES (110, 7, 22);
INSERT INTO `admin_role_permission` VALUES (111, 7, 23);
INSERT INTO `admin_role_permission` VALUES (112, 7, 24);
INSERT INTO `admin_role_permission` VALUES (113, 7, 25);
INSERT INTO `admin_role_permission` VALUES (114, 7, 26);
INSERT INTO `admin_role_permission` VALUES (115, 7, 28);
INSERT INTO `admin_role_permission` VALUES (116, 7, 29);
INSERT INTO `admin_role_permission` VALUES (117, 7, 35);
INSERT INTO `admin_role_permission` VALUES (118, 7, 39);
INSERT INTO `admin_role_permission` VALUES (119, 7, 40);
INSERT INTO `admin_role_permission` VALUES (120, 7, 41);
INSERT INTO `admin_role_permission` VALUES (121, 7, 42);
INSERT INTO `admin_role_permission` VALUES (122, 7, 48);
INSERT INTO `admin_role_permission` VALUES (123, 7, 49);
INSERT INTO `admin_role_permission` VALUES (124, 7, 50);
INSERT INTO `admin_role_permission` VALUES (125, 7, 51);
INSERT INTO `admin_role_permission` VALUES (126, 7, 52);
INSERT INTO `admin_role_permission` VALUES (134, 7, 53);

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt 加密密码',
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'admin', '$2a$10$cK.GzWtjyRgVYqNroGYfL.ZZq67g5IGn0xmVL47jKCdgqKdpwE7PS', '超级管理员', NULL, NULL, NULL, 1, '2026-06-28 14:12:02', '183.254.112.65', '系统初始化创建', '2026-06-24 10:52:38', '2026-06-28 14:12:02', 0);
INSERT INTO `admin_user` VALUES (4, 'yangailong', '$2a$10$Yw3CqSptt7o6.BZPfyg51u90hUyAbRpBWFqHQ1xMNPZGRjN00Xtde', '杨爱龙', NULL, NULL, NULL, 1, '2026-06-28 13:34:06', '183.254.112.65', NULL, '2026-06-28 10:39:41', '2026-06-28 13:34:06', 0);
INSERT INTO `admin_user` VALUES (5, 'guochaolin', '$2a$10$eC9qPk3O3mOM0H53gtz77eJmBUw7.gvtJ00a9IDbFU2XfD6JzTgwK', '郭超林', NULL, NULL, NULL, 1, '2026-06-28 14:09:57', '183.254.112.65', NULL, '2026-06-28 14:03:16', '2026-06-28 14:10:08', 0);

-- ----------------------------
-- Table structure for admin_user_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_user_role`;
CREATE TABLE `admin_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(0) NOT NULL,
  `role_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_role`(`admin_id`, `role_id`) USING BTREE,
  INDEX `idx_admin`(`admin_id`) USING BTREE,
  INDEX `idx_role`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user_role
-- ----------------------------
INSERT INTO `admin_user_role` VALUES (1, 1, 1);
INSERT INTO `admin_user_role` VALUES (2, 2, 7);
INSERT INTO `admin_user_role` VALUES (3, 3, 7);
INSERT INTO `admin_user_role` VALUES (4, 4, 7);
INSERT INTO `admin_user_role` VALUES (5, 5, 7);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置名称',
  `config_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分组',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
