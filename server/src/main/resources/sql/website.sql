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

 Date: 26/06/2026 23:07:19
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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台登录日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_login_log
-- ----------------------------
INSERT INTO `admin_login_log` VALUES (1, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-24 10:57:15');
INSERT INTO `admin_login_log` VALUES (2, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-24 10:57:21');
INSERT INTO `admin_login_log` VALUES (3, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-24 11:03:59');
INSERT INTO `admin_login_log` VALUES (4, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-24 11:11:47');
INSERT INTO `admin_login_log` VALUES (5, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36', 1, 1, '登录成功', '2026-06-24 16:27:21');
INSERT INTO `admin_login_log` VALUES (6, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 20:44:36');
INSERT INTO `admin_login_log` VALUES (7, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 20:44:59');
INSERT INTO `admin_login_log` VALUES (8, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 21:25:07');
INSERT INTO `admin_login_log` VALUES (9, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 21:36:47');
INSERT INTO `admin_login_log` VALUES (10, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 22:18:31');
INSERT INTO `admin_login_log` VALUES (11, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 22:33:57');
INSERT INTO `admin_login_log` VALUES (12, 1, 'admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 1, 1, '登录成功', '2026-06-26 22:58:46');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_operation_log
-- ----------------------------
INSERT INTO `admin_operation_log` VALUES (1, 1, 'admin', '商品管理', '保存商品', 'com.ydzz.admin.business.controller.GoodsController.save', '/api/admin/goods', 'POST', '[{\"id\":20,\"itemDesc\":\"命宫、身宫设置\",\"itemDiscount\":1,\"itemId\":\"10020\",\"itemPrice\":1,\"itemType\":3}]', NULL, '0:0:0:0:0:0:0:1', 103, 1, NULL, '2026-06-24 11:36:40');
INSERT INTO `admin_operation_log` VALUES (2, 1, 'admin', '角色管理', '删除角色', 'com.ydzz.admin.controller.AdminRoleController.remove', '/api/admin/role/5', 'DELETE', '[5]', NULL, '0:0:0:0:0:0:0:1', 358, 1, NULL, '2026-06-24 13:30:47');
INSERT INTO `admin_operation_log` VALUES (3, 1, 'admin', '角色管理', '删除角色', 'com.ydzz.admin.controller.AdminRoleController.remove', '/api/admin/role/4', 'DELETE', '[4]', NULL, '0:0:0:0:0:0:0:1', 5, 1, NULL, '2026-06-24 13:30:51');

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
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台权限/菜单' ROW_FORMAT = Dynamic;

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
INSERT INTO `admin_permission` VALUES (41, 'admin:list', '管理员管理', 2, 40, '/system/admin', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (42, 'admin:edit', '编辑管理员', 2, 41, '', NULL, '', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (43, 'role:list', '成员管理', 1, 40, '/system/role', NULL, '', 2, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (48, 'log:login', '登录日志', 1, 40, '/system/login-log', NULL, '', 5, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (49, 'log:operation', '操作日志', 1, 40, '/system/operation-log', NULL, '', 6, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_permission` VALUES (50, 'analytics:newdata', '新增数据', 1, 35, '/analytics/newdata', NULL, '', 2, 1, '2026-06-26 21:23:33', '2026-06-26 21:23:33', 0);
INSERT INTO `admin_permission` VALUES (51, 'analytics:payanalysis', '付费分析', 1, 35, '/analytics/payment', NULL, '', 3, 1, '2026-06-26 22:31:13', '2026-06-26 22:31:13', 0);
INSERT INTO `admin_permission` VALUES (52, 'analytics:active', '活跃数据', 1, 35, '/analytics/active', NULL, '', 5, 1, '2026-06-27 09:44:00', '2026-06-27 09:44:00', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role
-- ----------------------------
INSERT INTO `admin_role` VALUES (1, 'SUPER_ADMIN', '超级管理员', '拥有全部权限', 1, 1, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_role` VALUES (2, 'OPERATOR', '运营', '运营人员，内容/功能/活动编辑，数据查看', 1, 2, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_role` VALUES (3, 'SERVICE', '客服', '用户查看与禁用，订单查看', 1, 3, '2026-06-24 10:49:42', '2026-06-24 10:49:42', 0);
INSERT INTO `admin_role` VALUES (4, 'FINANCE', '财务', '订单/退款/收入相关', 1, 4, '2026-06-24 10:49:42', '2026-06-24 13:30:51', 1);
INSERT INTO `admin_role` VALUES (5, 'ANALYST', '数据分析', '数据分析查看', 1, 5, '2026-06-24 10:49:42', '2026-06-24 13:30:47', 1);
INSERT INTO `admin_role` VALUES (7, 'MEMBER', '普通成员', '普通成员：除角色增删改外拥有全部权限', 1, 7, '2026-06-27 12:00:00', '2026-06-27 12:00:00', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联' ROW_FORMAT = Dynamic;

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
-- 普通成员(role 7)：除「角色管理(role:list)」外的全部权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 7, `id` FROM `admin_permission` WHERE `permission_code` IS NULL OR `permission_code` <> 'role:list';

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'admin', '$2a$10$gZpaYmU6xLntKpC1QZ1bAuD5Cd3LrIoSqG8DgDA6PnQuTRGFD51.G', '超级管理员', NULL, NULL, NULL, 1, '2026-06-26 22:58:45', '0:0:0:0:0:0:0:1', '系统初始化创建', '2026-06-24 10:52:38', '2026-06-26 22:58:45', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user_role
-- ----------------------------
INSERT INTO `admin_user_role` VALUES (1, 1, 1);

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

-- ----------------------------
-- Table structure for visitinfo
-- ----------------------------
DROP TABLE IF EXISTS `visitinfo`;
CREATE TABLE `visitinfo`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国家',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `count` int(0) NULL DEFAULT 0 COMMENT '访问次数',
  `downInfo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载信息',
  `lastVisitTime` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上次访问时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of visitinfo
-- ----------------------------
INSERT INTO `visitinfo` VALUES ('1760514365709', '220.196.160.76', '中国', '常州市', '江苏省', 1, '[]', '1771316011');
INSERT INTO `visitinfo` VALUES ('1760523657361', '123.160.223.73', '中国', '郑州市', '河南省', 1, '[]', '1760820104');
INSERT INTO `visitinfo` VALUES ('1760525776008', '66.249.75.37', '美国', '芒廷维尤', '', 1, '[]', '1760927315');
INSERT INTO `visitinfo` VALUES ('1760541370079', '180.101.245.247', '中国', '南京市', '江苏省', 1, '[]', '1767232159');
INSERT INTO `visitinfo` VALUES ('1760548606507', '220.196.160.95', '中国', '常州市', '江苏省', 1, '[]', '1772289305');
INSERT INTO `visitinfo` VALUES ('1760567188417', '180.101.244.16', '中国', '南京市', '江苏省', 1, '[]', '1769505261');
INSERT INTO `visitinfo` VALUES ('1760579245191', '123.161.176.123', '中国', '许昌市', '河南省', 1, '[{\"downCount\":1,\"downTime\":\"1760579274\",\"platform\":\"Windows\"}]', '1760579245');
INSERT INTO `visitinfo` VALUES ('1760584848261', '171.10.118.58', '中国', '郑州市', '河南省', 1, '[]', '1760584848');
INSERT INTO `visitinfo` VALUES ('1760591845661', '123.119.28.165', '中国', '北京市', '北京市', 1, '[]', '1760591885');
INSERT INTO `visitinfo` VALUES ('1760595960387', '110.40.168.159', '中国', '北京市', '北京市', 1, '[]', '1760595960');
INSERT INTO `visitinfo` VALUES ('1760596239971', '182.254.103.53', '中国', '深圳市', '广东省', 1, '[]', '1760596239');
INSERT INTO `visitinfo` VALUES ('1760597751028', '220.196.160.53', '中国', '常州市', '江苏省', 1, '[]', '1769956179');
INSERT INTO `visitinfo` VALUES ('1760602645215', '112.193.54.178', '中国', '成都市', '四川省', 1, '[]', '1760602645');
INSERT INTO `visitinfo` VALUES ('1760605648750', '139.199.162.133', '中国', '深圳市', '广东省', 1, '[]', '1762324122');
INSERT INTO `visitinfo` VALUES ('1760605708289', '220.196.160.124', '中国', '常州市', '江苏省', 1, '[]', '1775654151');
INSERT INTO `visitinfo` VALUES ('1760616545796', '111.7.100.25', '中国', '驻马店市', '河南省', 1, '[]', '1760616545');
INSERT INTO `visitinfo` VALUES ('1760616928512', '1.202.12.35', '中国', '北京市', '北京市', 1, '[]', '1760616928');
INSERT INTO `visitinfo` VALUES ('1760619411572', '111.7.100.27', '中国', '驻马店市', '河南省', 1, '[]', '1760619411');
INSERT INTO `visitinfo` VALUES ('1760626020393', '111.17.50.237', '中国', '泰安市', '山东省', 1, '[]', '1760664024');
INSERT INTO `visitinfo` VALUES ('1760626027511', '114.249.105.0', '中国', '北京市', '北京市', 1, '[{\"downCount\":3,\"downTime\":\"1761052072\",\"platform\":\"Android\"}]', '1761052064');
INSERT INTO `visitinfo` VALUES ('1760660903798', '220.196.160.146', '中国', '常州市', '江苏省', 1, '[]', '1780478178');
INSERT INTO `visitinfo` VALUES ('1760663773870', '59.83.208.105', '中国', '济南市', '山东省', 1, '[]', '1776258028');
INSERT INTO `visitinfo` VALUES ('1760670451175', '123.117.167.55', '中国', '北京市', '北京市', 1, '[]', '1760692340');
INSERT INTO `visitinfo` VALUES ('1760688467664', '210.12.130.222', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1760688486\",\"platform\":\"Windows\"}]', '1760688467');
INSERT INTO `visitinfo` VALUES ('1760689498758', '114.242.60.44', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1761018724\",\"platform\":\"Windows\"}]', '1761018956');
INSERT INTO `visitinfo` VALUES ('1760691740190', '117.136.0.123', '中国', '北京市', '北京市', 1, '[{\"downCount\":2,\"downTime\":\"1760691744\",\"platform\":\"Android\"}]', '1760691743');
INSERT INTO `visitinfo` VALUES ('1760692964572', '182.254.115.244', '中国', '深圳市', '广东省', 1, '[]', '1760692964');
INSERT INTO `visitinfo` VALUES ('1760693936299', '124.222.3.198', '中国', '北京市', '北京市', 1, '[]', '1760693936');
INSERT INTO `visitinfo` VALUES ('1760696105760', '124.127.68.158', '中国', '北京市', '北京市', 1, '[]', '1760696105');
INSERT INTO `visitinfo` VALUES ('1760698243730', '114.244.127.138', '中国', '北京市', '北京市', 1, '[]', '1760698243');
INSERT INTO `visitinfo` VALUES ('1760701348817', '183.254.111.198', '中国', '海口市', '海南省', 1, '[{\"downCount\":13,\"downTime\":\"1761232350\",\"platform\":\"Windows\"}]', '1761233479');
INSERT INTO `visitinfo` VALUES ('1760703351084', '117.136.0.19', '中国', '北京市', '北京市', 1, '[]', '1760703351');
INSERT INTO `visitinfo` VALUES ('1760704460303', '27.44.125.106', '中国', '东莞市', '广东省', 1, '[]', '1760704460');
INSERT INTO `visitinfo` VALUES ('1760705527721', '39.171.91.238', '中国', '台州市', '浙江省', 1, '[]', '1760705580');
INSERT INTO `visitinfo` VALUES ('1760708134199', '221.223.159.209', '中国', '北京市', '北京市', 1, '[{\"downCount\":2,\"downTime\":\"1760709682\",\"platform\":\"Android\"}]', '1760709680');
INSERT INTO `visitinfo` VALUES ('1760712703697', '115.60.57.54', '中国', '郑州市', '河南省', 1, '[{\"downCount\":3,\"downTime\":\"1760712709\",\"platform\":\"Android\"}]', '1760712860');
INSERT INTO `visitinfo` VALUES ('1760713942521', '111.7.100.21', '中国', '驻马店市', '河南省', 1, '[]', '1760713942');
INSERT INTO `visitinfo` VALUES ('1760752119962', '106.117.114.208', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":1,\"downTime\":\"1760752133\",\"platform\":\"Android\"}]', '1760752131');
INSERT INTO `visitinfo` VALUES ('1760755640365', '104.253.247.207', '美国', '圣克拉拉', '', 1, '[]', '1760755640');
INSERT INTO `visitinfo` VALUES ('1760755641126', '104.252.110.44', '美国', '圣克拉拉', '', 1, '[]', '1760755641');
INSERT INTO `visitinfo` VALUES ('1760755641396', '103.4.250.227', '美国', '紐約', '', 1, '[]', '1760755641');
INSERT INTO `visitinfo` VALUES ('1760760523609', '117.129.43.39', '中国', '北京市', '北京市', 1, '[]', '1760760523');
INSERT INTO `visitinfo` VALUES ('1760813784691', '180.101.245.252', '中国', '南京市', '江苏省', 1, '[]', '1762869201');
INSERT INTO `visitinfo` VALUES ('1760813788820', '180.101.244.14', '中国', '南京市', '江苏省', 1, '[]', '1776747277');
INSERT INTO `visitinfo` VALUES ('1760820103882', '123.160.223.72', '中国', '郑州市', '河南省', 1, '[]', '1760820103');
INSERT INTO `visitinfo` VALUES ('1760820113188', '36.41.67.60', '中国', '咸阳市', '陕西省', 1, '[]', '1764565364');
INSERT INTO `visitinfo` VALUES ('1760820131451', '36.41.65.20', '中国', '咸阳市', '陕西省', 1, '[]', '1760820131');
INSERT INTO `visitinfo` VALUES ('1760882676620', '223.166.14.244', '中国', '上海市', '上海市', 1, '[]', '1760882676');
INSERT INTO `visitinfo` VALUES ('1760901426385', '66.249.75.36', '美国', '芒廷维尤', '', 1, '[]', '1760901426');
INSERT INTO `visitinfo` VALUES ('1760921636839', '115.60.57.138', '中国', '郑州市', '河南省', 1, '[{\"downCount\":1,\"downTime\":\"1760921643\",\"platform\":\"Android\"}]', '1760921636');
INSERT INTO `visitinfo` VALUES ('1760924641777', '223.71.40.74', '中国', '北京市', '北京市', 1, '[]', '1760924641');
INSERT INTO `visitinfo` VALUES ('1760926115609', '111.198.15.138', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1760926128\",\"platform\":\"Windows\"}]', '1760926115');
INSERT INTO `visitinfo` VALUES ('1760934265075', '125.35.5.254', '中国', '北京市', '北京市', 1, '[]', '1760934265');
INSERT INTO `visitinfo` VALUES ('1760940033361', '123.103.9.136', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1760940038\",\"platform\":\"Windows\"}]', '1760940033');
INSERT INTO `visitinfo` VALUES ('1760941994152', '221.223.85.137', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1761012877\",\"platform\":\"Android\"}]', '1761042656');
INSERT INTO `visitinfo` VALUES ('1760950237691', '220.196.160.75', '中国', '常州市', '江苏省', 1, '[]', '1776859733');
INSERT INTO `visitinfo` VALUES ('1760973819136', '27.44.122.236', '中国', '东莞市', '广东省', 1, '[]', '1760973819');
INSERT INTO `visitinfo` VALUES ('1760981328609', '220.196.160.84', '中国', '常州市', '江苏省', 1, '[]', '1776144643');
INSERT INTO `visitinfo` VALUES ('1760984132543', '180.101.244.12', '中国', '南京市', '江苏省', 1, '[]', '1779875479');
INSERT INTO `visitinfo` VALUES ('1760989006287', '220.196.160.65', '中国', '常州市', '江苏省', 1, '[]', '1775541390');
INSERT INTO `visitinfo` VALUES ('1761022037954', '59.83.208.104', '中国', '济南市', '山东省', 1, '[]', '1779271707');
INSERT INTO `visitinfo` VALUES ('1761031880865', '59.46.216.137', '中国', '大连市', '辽宁省', 1, '[]', '1761031880');
INSERT INTO `visitinfo` VALUES ('1761042746051', '222.172.32.50', '中国', '黑河市', '黑龙江省', 1, '[]', '1761042746');
INSERT INTO `visitinfo` VALUES ('1761044452496', '42.92.149.251', '中国', '兰州市', '甘肃省', 1, '[]', '1761046508');
INSERT INTO `visitinfo` VALUES ('1761045827051', '110.40.153.169', '中国', '北京市', '北京市', 1, '[]', '1761045827');
INSERT INTO `visitinfo` VALUES ('1761047214856', '183.193.27.116', '中国', '上海市', '上海市', 1, '[]', '1761047214');
INSERT INTO `visitinfo` VALUES ('1761059200982', '220.196.160.73', '中国', '常州市', '江苏省', 1, '[]', '1772375770');
INSERT INTO `visitinfo` VALUES ('1761063510481', '180.101.245.250', '中国', '南京市', '江苏省', 1, '[]', '1775054746');
INSERT INTO `visitinfo` VALUES ('1761091357812', '27.44.125.108', '中国', '东莞市', '广东省', 1, '[]', '1761091357');
INSERT INTO `visitinfo` VALUES ('1761114109547', '125.122.33.81', '中国', '杭州市', '浙江省', 1, '[]', '1761114109');
INSERT INTO `visitinfo` VALUES ('1761114117287', '113.141.85.73', '中国', '西安市', '陕西省', 1, '[]', '1761114117');
INSERT INTO `visitinfo` VALUES ('1761114128145', '125.122.13.182', '中国', '杭州市', '浙江省', 1, '[]', '1761114216');
INSERT INTO `visitinfo` VALUES ('1761116230295', '180.101.244.13', '中国', '南京市', '江苏省', 1, '[]', '1776859734');
INSERT INTO `visitinfo` VALUES ('1761144182581', '14.112.8.231', '中国', '惠州市', '广东省', 1, '[{\"downCount\":1,\"downTime\":\"1761144183\",\"platform\":\"Android\"}]', '1761144182');
INSERT INTO `visitinfo` VALUES ('1761237537947', '104.253.252.14', '美国', '圣克拉拉', '', 1, '[]', '1761237537');
INSERT INTO `visitinfo` VALUES ('1761237541321', '104.253.247.121', '美国', '圣克拉拉', '', 1, '[]', '1761237541');
INSERT INTO `visitinfo` VALUES ('1761282339891', '183.255.46.43', '中国', '海口市', '海南省', 1, '[{\"downCount\":6,\"downTime\":\"1779175781\",\"platform\":\"Windows\"}]', '1779862879');
INSERT INTO `visitinfo` VALUES ('1761291695047', '221.223.40.236', '中国', '北京市', '北京市', 1, '[{\"downCount\":8,\"downTime\":\"1762831277\",\"platform\":\"Android\"}]', '1762844172');
INSERT INTO `visitinfo` VALUES ('1761291712181', '114.251.196.101', '中国', '北京市', '北京市', 1, '[]', '1761291712');
INSERT INTO `visitinfo` VALUES ('1761293071117', '183.46.168.207', '中国', '汕头市', '广东省', 1, '[]', '1761293071');
INSERT INTO `visitinfo` VALUES ('1761294671887', '183.14.29.38', '中国', '深圳市', '广东省', 1, '[]', '1761294671');
INSERT INTO `visitinfo` VALUES ('1761310653358', '123.118.73.41', '中国', '北京市', '北京市', 1, '[{\"downCount\":5,\"downTime\":\"1761311342\",\"platform\":\"Android\"}]', '1761311341');
INSERT INTO `visitinfo` VALUES ('1761345875265', '57.141.2.95', '美国', '聖荷西', '', 1, '[]', '1761345875');
INSERT INTO `visitinfo` VALUES ('1761435577256', '66.249.75.34', '美国', '芒廷维尤', '', 1, '[]', '1779377321');
INSERT INTO `visitinfo` VALUES ('1761457288108', '18.223.252.168', '美国', 'Dublin', '', 1, '[]', '1761457288');
INSERT INTO `visitinfo` VALUES ('1761474101171', '111.7.100.24', '中国', '驻马店市', '河南省', 1, '[]', '1761474101');
INSERT INTO `visitinfo` VALUES ('1761485083140', '111.172.7.109', '中国', '武汉市', '湖北省', 1, '[]', '1761485083');
INSERT INTO `visitinfo` VALUES ('1761485648961', '111.194.72.238', '中国', '北京市', '北京市', 1, '[{\"downCount\":5,\"downTime\":\"1762353953\",\"platform\":\"Android\"}]', '1762436135');
INSERT INTO `visitinfo` VALUES ('1761485675564', '36.98.204.208', '中国', '石家庄市', '河北省', 1, '[]', '1761486780');
INSERT INTO `visitinfo` VALUES ('1761485677905', '218.12.16.57', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":6,\"downTime\":\"1761485750\",\"platform\":\"Android\"}]', '1761485748');
INSERT INTO `visitinfo` VALUES ('1761489809145', '218.12.20.21', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":2,\"downTime\":\"1761489993\",\"platform\":\"Android\"}]', '1762763649');
INSERT INTO `visitinfo` VALUES ('1761495294020', '111.7.100.22', '中国', '驻马店市', '河南省', 1, '[]', '1761495294');
INSERT INTO `visitinfo` VALUES ('1761568808403', '221.221.56.23', '中国', '北京市', '北京市', 1, '[{\"downCount\":3,\"downTime\":\"1761569198\",\"platform\":\"Android\"}]', '1761569193');
INSERT INTO `visitinfo` VALUES ('1761580275860', '57.141.2.24', '美国', '聖荷西', '', 1, '[]', '1761580275');
INSERT INTO `visitinfo` VALUES ('1761581547262', '57.141.2.88', '美国', '聖荷西', '', 1, '[]', '1761581547');
INSERT INTO `visitinfo` VALUES ('1761617701108', '57.141.2.1', '美国', '聖荷西', '', 1, '[]', '1761617701');
INSERT INTO `visitinfo` VALUES ('1761640447531', '39.152.133.237', '中国', '沈阳市', '辽宁省', 1, '[]', '1761640447');
INSERT INTO `visitinfo` VALUES ('1761657060091', '57.141.2.10', '美国', '聖荷西', '', 1, '[]', '1761657060');
INSERT INTO `visitinfo` VALUES ('1761660253838', '220.196.160.83', '中国', '常州市', '江苏省', 1, '[]', '1772030110');
INSERT INTO `visitinfo` VALUES ('1761661675217', '220.196.160.125', '中国', '常州市', '江苏省', 1, '[]', '1777463114');
INSERT INTO `visitinfo` VALUES ('1761667908981', '57.141.2.18', '美国', '聖荷西', '', 1, '[]', '1761667908');
INSERT INTO `visitinfo` VALUES ('1761670096432', '62.210.90.187', '法国', '巴黎', '', 1, '[]', '1761670096');
INSERT INTO `visitinfo` VALUES ('1761671046433', '51.159.214.65', '法国', '巴黎', '', 1, '[]', '1761671046');
INSERT INTO `visitinfo` VALUES ('1761694954350', '183.254.112.255', '中国', '海口市', '海南省', 1, '[{\"downCount\":8,\"downTime\":\"1762070971\",\"platform\":\"Windows\"}]', '1762071493');
INSERT INTO `visitinfo` VALUES ('1761727806549', '106.47.3.209', '中国', '天津市', '天津市', 1, '[{\"downCount\":1,\"downTime\":\"1761727912\",\"platform\":\"Android\"}]', '1761727806');
INSERT INTO `visitinfo` VALUES ('1761733977963', '220.181.3.189', '中国', '北京市', '北京市', 1, '[]', '1773813996');
INSERT INTO `visitinfo` VALUES ('1761797156941', '223.72.37.222', '中国', '北京市', '北京市', 1, '[{\"downCount\":3,\"downTime\":\"1761797286\",\"platform\":\"Windows\"}]', '1761797274');
INSERT INTO `visitinfo` VALUES ('1761804165781', '111.196.219.255', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1761804209\",\"platform\":\"Windows\"}]', '1761804165');
INSERT INTO `visitinfo` VALUES ('1761811062712', '180.101.244.15', '中国', '南京市', '江苏省', 1, '[]', '1773672682');
INSERT INTO `visitinfo` VALUES ('1761841625297', '57.141.2.34', '美国', '聖荷西', '', 1, '[]', '1761841625');
INSERT INTO `visitinfo` VALUES ('1761885210569', '123.57.130.57', '中国', '北京市', '北京市', 1, '[]', '1761885210');
INSERT INTO `visitinfo` VALUES ('1761892000287', '117.163.62.210', '中国', '南昌市', '江西省', 1, '[]', '1761892000');
INSERT INTO `visitinfo` VALUES ('1761902200386', '125.122.15.52', '中国', '杭州市', '浙江省', 1, '[]', '1761902200');
INSERT INTO `visitinfo` VALUES ('1761929664247', '57.141.14.16', '爱尔兰', '都柏林', '', 1, '[]', '1761929664');
INSERT INTO `visitinfo` VALUES ('1761929733495', '57.141.14.92', '爱尔兰', '都柏林', '', 1, '[]', '1761929733');
INSERT INTO `visitinfo` VALUES ('1762013612949', '57.141.14.33', '爱尔兰', '都柏林', '', 1, '[]', '1762013612');
INSERT INTO `visitinfo` VALUES ('1762023670087', '66.249.75.35', '美国', '芒廷维尤', '', 1, '[]', '1779408315');
INSERT INTO `visitinfo` VALUES ('1762172395018', '49.12.6.164', '德国', 'Falkenstein', '', 1, '[]', '1762172395');
INSERT INTO `visitinfo` VALUES ('1762189226250', '203.195.164.241', '中国', '广州市', '广东省', 1, '[]', '1773138237');
INSERT INTO `visitinfo` VALUES ('1762226120336', '183.46.195.15', '中国', '汕头市', '广东省', 1, '[{\"downCount\":1,\"downTime\":\"1762226121\",\"platform\":\"Android\"}]', '1762226120');
INSERT INTO `visitinfo` VALUES ('1762240585600', '59.37.125.39', '中国', '深圳市', '广东省', 1, '[]', '1762240689');
INSERT INTO `visitinfo` VALUES ('1762253011622', '183.198.3.158', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":1,\"downTime\":\"1762253043\",\"platform\":\"Android\"}]', '1762253029');
INSERT INTO `visitinfo` VALUES ('1762269308472', '64.233.172.5', '美国', '芒廷维尤', '', 1, '[]', '1762269308');
INSERT INTO `visitinfo` VALUES ('1762277478733', '113.141.83.185', '中国', '西安市', '陕西省', 1, '[]', '1764457911');
INSERT INTO `visitinfo` VALUES ('1762306117309', '1.202.13.169', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1762312818\",\"platform\":\"Android\"}]', '1762312819');
INSERT INTO `visitinfo` VALUES ('1762312806206', '36.112.85.251', '中国', '杭州市', '浙江省', 1, '[{\"downCount\":1,\"downTime\":\"1762312806\",\"platform\":\"Android\"}]', '1762312806');
INSERT INTO `visitinfo` VALUES ('1762314778615', '114.254.0.165', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1762314908\",\"platform\":\"Android\"}]', '1762314905');
INSERT INTO `visitinfo` VALUES ('1762344667524', '202.40.221.132', '香港', '旺角', '', 1, '[]', '1762344667');
INSERT INTO `visitinfo` VALUES ('1762414757315', '180.101.245.246', '中国', '南京市', '江苏省', 1, '[]', '1779875476');
INSERT INTO `visitinfo` VALUES ('1762414757938', '220.196.160.51', '中国', '常州市', '江苏省', 1, '[]', '1775051692');
INSERT INTO `visitinfo` VALUES ('1762524173747', '59.83.208.108', '中国', '济南市', '山东省', 1, '[]', '1776144653');
INSERT INTO `visitinfo` VALUES ('1762595030941', '123.160.221.131', '中国', '郑州市', '河南省', 1, '[]', '1773761308');
INSERT INTO `visitinfo` VALUES ('1762685915298', '158.247.224.190', '韩国', '首尔特别市', '', 1, '[{\"downCount\":2,\"downTime\":\"1762783639\",\"platform\":\"Windows\"}]', '1762874065');
INSERT INTO `visitinfo` VALUES ('1762746443726', '223.76.232.210', '中国', '黄石市', '湖北省', 1, '[]', '1762746443');
INSERT INTO `visitinfo` VALUES ('1762770992959', '111.29.130.17', '中国', '海口市', '海南省', 1, '[{\"downCount\":7,\"downTime\":\"1762781046\",\"platform\":\"Android\"}]', '1762784063');
INSERT INTO `visitinfo` VALUES ('1762774196134', '17.241.219.21', '美国', '庫比蒂諾', '', 1, '[]', '1762774196');
INSERT INTO `visitinfo` VALUES ('1762775012645', '17.22.245.201', '美国', '庫比蒂諾', '', 1, '[]', '1762775012');
INSERT INTO `visitinfo` VALUES ('1762781389475', '114.249.133.25', '中国', '北京市', '北京市', 1, '[{\"downCount\":7,\"downTime\":\"1764165113\",\"platform\":\"Android\"}]', '1764257761');
INSERT INTO `visitinfo` VALUES ('1762783446803', '180.101.245.251', '中国', '南京市', '江苏省', 1, '[]', '1768298000');
INSERT INTO `visitinfo` VALUES ('1762787722605', '14.112.36.151', '中国', '惠州市', '广东省', 1, '[{\"downCount\":2,\"downTime\":\"1762787739\",\"platform\":\"Android\"}]', '1762787722');
INSERT INTO `visitinfo` VALUES ('1762836536173', '114.246.236.79', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1762836542\",\"platform\":\"Android\"}]', '1762836659');
INSERT INTO `visitinfo` VALUES ('1762874181437', '69.166.125.72', '加拿大', '密西沙加', '', 1, '[]', '1762874181');
INSERT INTO `visitinfo` VALUES ('1762916572044', '13.221.86.251', '美国', 'Ashburn', '', 1, '[]', '1762916572');
INSERT INTO `visitinfo` VALUES ('1762951351937', '123.116.234.188', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1762951353\",\"platform\":\"Android\"}]', '1763211068');
INSERT INTO `visitinfo` VALUES ('1763025752402', '220.196.160.96', '中国', '常州市', '江苏省', 1, '[]', '1775654152');
INSERT INTO `visitinfo` VALUES ('1763062997490', '65.55.210.69', '美国', '昆西', '', 1, '[]', '1763062997');
INSERT INTO `visitinfo` VALUES ('1763093961262', '66.249.72.199', '美国', '芒廷维尤', '', 1, '[]', '1763093961');
INSERT INTO `visitinfo` VALUES ('1763109668982', '221.223.85.138', '中国', '北京市', '北京市', 1, '[]', '1763441406');
INSERT INTO `visitinfo` VALUES ('1763109693441', '183.196.11.203', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":2,\"downTime\":\"1763109769\",\"platform\":\"Android\"}]', '1763109752');
INSERT INTO `visitinfo` VALUES ('1763110051711', '123.160.221.133', '中国', '郑州市', '河南省', 1, '[]', '1769229521');
INSERT INTO `visitinfo` VALUES ('1763110140349', '218.12.18.49', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":2,\"downTime\":\"1763110192\",\"platform\":\"Android\"}]', '1763110190');
INSERT INTO `visitinfo` VALUES ('1763110243457', '218.12.21.18', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":2,\"downTime\":\"1763110351\",\"platform\":\"Android\"}]', '1763110335');
INSERT INTO `visitinfo` VALUES ('1763201713310', '49.65.155.215', '中国', '南京市', '江苏省', 1, '[{\"downCount\":2,\"downTime\":\"1763201731\",\"platform\":\"Android\"}]', '1763201713');
INSERT INTO `visitinfo` VALUES ('1763262321538', '61.171.220.89', '中国', '上海市', '上海市', 1, '[]', '1763262321');
INSERT INTO `visitinfo` VALUES ('1763310622737', '111.172.6.233', '中国', '武汉市', '湖北省', 1, '[]', '1763310622');
INSERT INTO `visitinfo` VALUES ('1763343055090', '113.141.86.22', '中国', '西安市', '陕西省', 1, '[]', '1763343055');
INSERT INTO `visitinfo` VALUES ('1763350174680', '114.246.239.66', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1763350176\",\"platform\":\"Android\"}]', '1763350174');
INSERT INTO `visitinfo` VALUES ('1763361337679', '221.216.116.97', '中国', '北京市', '北京市', 1, '[]', '1763391783');
INSERT INTO `visitinfo` VALUES ('1763472135691', '220.196.160.101', '中国', '常州市', '江苏省', 1, '[]', '1777463115');
INSERT INTO `visitinfo` VALUES ('1763561344544', '220.196.160.144', '中国', '常州市', '江苏省', 1, '[]', '1776747280');
INSERT INTO `visitinfo` VALUES ('1763598163238', '27.159.75.49', '中国', '厦门市', '福建省', 1, '[]', '1769220810');
INSERT INTO `visitinfo` VALUES ('1763685373028', '66.249.75.234', '美国', '芒廷维尤', '', 1, '[]', '1763685373');
INSERT INTO `visitinfo` VALUES ('1763813452989', '111.29.128.136', '中国', '海口市', '海南省', 1, '[{\"downCount\":5,\"downTime\":\"1764173895\",\"platform\":\"Android\"}]', '1764335574');
INSERT INTO `visitinfo` VALUES ('1763899560933', '106.47.1.246', '中国', '天津市', '天津市', 1, '[{\"downCount\":1,\"downTime\":\"1763899561\",\"platform\":\"Android\"}]', '1763899560');
INSERT INTO `visitinfo` VALUES ('1763899770542', '119.249.100.89', '中国', '保定市', '河北省', 1, '[]', '1763899770');
INSERT INTO `visitinfo` VALUES ('1764029615085', '66.249.69.227', '美国', '芒廷维尤', '', 1, '[]', '1764029615');
INSERT INTO `visitinfo` VALUES ('1764034119777', '66.249.69.226', '美国', '芒廷维尤', '', 1, '[]', '1764430017');
INSERT INTO `visitinfo` VALUES ('1764076434450', '220.196.160.151', '中国', '常州市', '江苏省', 1, '[]', '1767232158');
INSERT INTO `visitinfo` VALUES ('1764121568149', '110.251.1.15', '中国', '廊坊市', '河北省', 1, '[{\"downCount\":1,\"downTime\":\"1764121569\",\"platform\":\"Android\"}]', '1764121568');
INSERT INTO `visitinfo` VALUES ('1764139926736', '221.223.47.27', '中国', '北京市', '北京市', 1, '[{\"downCount\":9,\"downTime\":\"1766372236\",\"platform\":\"Android\"}]', '1766629199');
INSERT INTO `visitinfo` VALUES ('1764146055816', '36.99.136.138', '中国', '驻马店市', '河南省', 1, '[]', '1764146055');
INSERT INTO `visitinfo` VALUES ('1764159077580', '218.12.20.104', '中国', '石家庄市', '河北省', 1, '[{\"downCount\":3,\"downTime\":\"1764227573\",\"platform\":\"Windows\"}]', '1764227565');
INSERT INTO `visitinfo` VALUES ('1764159367153', '113.141.80.44', '中国', '西安市', '陕西省', 1, '[]', '1764159367');
INSERT INTO `visitinfo` VALUES ('1764162550103', '154.12.186.19', '美国', '聖荷西', '', 1, '[{\"downCount\":3,\"downTime\":\"1764686940\",\"platform\":\"Windows\"}]', '1764686835');
INSERT INTO `visitinfo` VALUES ('1764171192969', '124.160.197.154', '中国', '杭州市', '浙江省', 1, '[{\"downCount\":1,\"downTime\":\"1764171208\",\"platform\":\"Windows\"}]', '1764171192');
INSERT INTO `visitinfo` VALUES ('1764174695861', '124.160.201.40', '中国', '杭州市', '浙江省', 1, '[{\"downCount\":2,\"downTime\":\"1764174780\",\"platform\":\"Android\"}]', '1764174779');
INSERT INTO `visitinfo` VALUES ('1764180465494', '111.172.5.156', '中国', '武汉市', '湖北省', 1, '[]', '1764188862');
INSERT INTO `visitinfo` VALUES ('1764206045460', '61.148.191.174', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1764206049\",\"platform\":\"Android\"}]', '1764206045');
INSERT INTO `visitinfo` VALUES ('1764209561162', '114.254.1.176', '中国', '北京市', '北京市', 1, '[{\"downCount\":4,\"downTime\":\"1764210861\",\"platform\":\"Android\"}]', '1764210727');
INSERT INTO `visitinfo` VALUES ('1764232023477', '180.101.245.248', '中国', '南京市', '江苏省', 1, '[]', '1773126382');
INSERT INTO `visitinfo` VALUES ('1764265105803', '123.57.204.245', '中国', '北京市', '北京市', 1, '[]', '1764265105');
INSERT INTO `visitinfo` VALUES ('1764385713859', '183.254.110.228', '中国', '海口市', '海南省', 1, '[{\"downCount\":5,\"downTime\":\"1764421388\",\"platform\":\"Android\"}]', '1765009912');
INSERT INTO `visitinfo` VALUES ('1764418669708', '117.136.0.112', '中国', '北京市', '北京市', 1, '[{\"downCount\":2,\"downTime\":\"1764423829\",\"platform\":\"Android\"}]', '1764423820');
INSERT INTO `visitinfo` VALUES ('1764470544889', '112.96.208.198', '中国', '广州市', '广东省', 1, '[]', '1764470544');
INSERT INTO `visitinfo` VALUES ('1764470547007', '192.0.84.159', '美国', '達拉斯', '', 1, '[]', '1764470547');
INSERT INTO `visitinfo` VALUES ('1764470733419', '223.87.230.150', '中国', '成都市', '四川省', 1, '[]', '1764470733');
INSERT INTO `visitinfo` VALUES ('1764470738509', '39.64.177.118', '中国', '济南市', '山东省', 1, '[]', '1764470738');
INSERT INTO `visitinfo` VALUES ('1764572301216', '149.56.160.133', '加拿大', '蒙特利尔', '', 1, '[]', '1764572301');
INSERT INTO `visitinfo` VALUES ('1764598478630', '59.83.208.106', '中国', '济南市', '山东省', 1, '[]', '1773931094');
INSERT INTO `visitinfo` VALUES ('1764664773839', '1.202.193.226', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1764664774\",\"platform\":\"Android\"}]', '1764664773');
INSERT INTO `visitinfo` VALUES ('1764679095148', '180.101.245.253', '中国', '南京市', '江苏省', 1, '[]', '1764679095');
INSERT INTO `visitinfo` VALUES ('1764689969198', '223.72.38.119', '中国', '北京市', '北京市', 1, '[]', '1764689969');
INSERT INTO `visitinfo` VALUES ('1764695790505', '157.90.245.150', '德国', 'Falkenstein', '', 1, '[]', '1764695790');
INSERT INTO `visitinfo` VALUES ('1764732206013', '114.254.1.165', '中国', '北京市', '北京市', 1, '[]', '1764732206');
INSERT INTO `visitinfo` VALUES ('1764754450515', '113.141.83.227', '中国', '西安市', '陕西省', 1, '[]', '1772341407');
INSERT INTO `visitinfo` VALUES ('1764755869905', '123.121.230.185', '中国', '北京市', '北京市', 1, '[]', '1764755869');
INSERT INTO `visitinfo` VALUES ('1764834836631', '220.196.160.61', '中国', '常州市', '江苏省', 1, '[]', '1771252329');
INSERT INTO `visitinfo` VALUES ('1764935125093', '49.13.123.185', '德国', 'Falkenstein', '', 1, '[]', '1764935125');
INSERT INTO `visitinfo` VALUES ('1764949020925', '111.194.75.221', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1764949025\",\"platform\":\"Android\"}]', '1764999248');
INSERT INTO `visitinfo` VALUES ('1764992178969', '120.10.124.212', '中国', '沧州市', '河北省', 1, '[]', '1764992178');
INSERT INTO `visitinfo` VALUES ('1765012262769', '125.122.32.22', '中国', '杭州市', '浙江省', 1, '[]', '1765012262');
INSERT INTO `visitinfo` VALUES ('1765012662128', '61.54.31.122', '中国', '洛阳市', '河南省', 1, '[]', '1765188950');
INSERT INTO `visitinfo` VALUES ('1765105580720', '183.254.111.122', '中国', '海口市', '海南省', 1, '[{\"downCount\":6,\"downTime\":\"1765610199\",\"platform\":\"Windows\"}]', '1765618704');
INSERT INTO `visitinfo` VALUES ('1765171655704', '153.3.251.213', '中国', '南京市', '江苏省', 1, '[]', '1765171655');
INSERT INTO `visitinfo` VALUES ('1765172878434', '153.3.251.219', '中国', '南京市', '江苏省', 1, '[]', '1765172878');
INSERT INTO `visitinfo` VALUES ('1765209667192', '123.115.184.199', '中国', '北京市', '北京市', 1, '[]', '1766501965');
INSERT INTO `visitinfo` VALUES ('1765238411836', '36.41.71.44', '中国', '咸阳市', '陕西省', 1, '[]', '1765238411');
INSERT INTO `visitinfo` VALUES ('1765295007922', '40.77.179.23', '美国', '昆西', '', 1, '[]', '1765295007');
INSERT INTO `visitinfo` VALUES ('1765345617149', '36.41.68.148', '中国', '咸阳市', '陕西省', 1, '[]', '1765345617');
INSERT INTO `visitinfo` VALUES ('1765588665277', '66.249.69.162', '美国', '芒廷维尤', '', 1, '[]', '1771028127');
INSERT INTO `visitinfo` VALUES ('1765625612212', '49.7.4.57', '中国', '北京市', '北京市', 1, '[]', '1765625612');
INSERT INTO `visitinfo` VALUES ('1765625613182', '114.246.236.153', '中国', '北京市', '北京市', 1, '[]', '1765625613');
INSERT INTO `visitinfo` VALUES ('1765745504737', '111.172.7.1', '中国', '武汉市', '湖北省', 1, '[]', '1765745507');
INSERT INTO `visitinfo` VALUES ('1765871391932', '59.37.125.37', '中国', '深圳市', '广东省', 1, '[{\"downCount\":2,\"downTime\":\"1765871413\",\"platform\":\"Android\"}]', '1765871391');
INSERT INTO `visitinfo` VALUES ('1765886424826', '220.196.160.117', '中国', '常州市', '江苏省', 1, '[]', '1775832881');
INSERT INTO `visitinfo` VALUES ('1765899904776', '125.122.33.107', '中国', '杭州市', '浙江省', 1, '[]', '1765899904');
INSERT INTO `visitinfo` VALUES ('1765901092267', '120.245.5.114', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1765901094\",\"platform\":\"Android\"}]', '1765901092');
INSERT INTO `visitinfo` VALUES ('1765935988223', '124.152.137.206', '中国', '平凉市', '甘肃省', 1, '[]', '1765935988');
INSERT INTO `visitinfo` VALUES ('1766053020023', '66.249.75.66', '美国', '奥斯卡卢萨', '', 1, '[]', '1766949637');
INSERT INTO `visitinfo` VALUES ('1766208884807', '66.249.75.68', '美国', '奥斯卡卢萨', '', 1, '[]', '1766208884');
INSERT INTO `visitinfo` VALUES ('1766232464164', '117.136.0.108', '中国', '北京市', '北京市', 1, '[]', '1766232502');
INSERT INTO `visitinfo` VALUES ('1766337696125', '153.35.209.46', '中国', '南京市', '江苏省', 1, '[]', '1766337696');
INSERT INTO `visitinfo` VALUES ('1766343590852', '111.172.4.27', '中国', '武汉市', '湖北省', 1, '[]', '1766343590');
INSERT INTO `visitinfo` VALUES ('1766348145198', '113.141.86.166', '中国', '西安市', '陕西省', 1, '[]', '1766348145');
INSERT INTO `visitinfo` VALUES ('1766397828388', '14.21.235.193', '中国', '中山市', '广东省', 1, '[{\"downCount\":1,\"downTime\":\"1766397836\",\"platform\":\"Windows\"}]', '1766397828');
INSERT INTO `visitinfo` VALUES ('1766489420508', '59.83.208.103', '中国', '济南市', '山东省', 1, '[]', '1780478184');
INSERT INTO `visitinfo` VALUES ('1766545367140', '223.96.139.172', '中国', '枣庄市', '山东省', 1, '[{\"downCount\":1,\"downTime\":\"1766545389\",\"platform\":\"Android\"}]', '1766545367');
INSERT INTO `visitinfo` VALUES ('1767210802976', '113.141.95.83', '中国', '西安市', '陕西省', 1, '[]', '1767210802');
INSERT INTO `visitinfo` VALUES ('1767219389358', '66.249.69.163', '美国', '芒廷维尤', '', 1, '[]', '1779759911');
INSERT INTO `visitinfo` VALUES ('1767296458652', '149.56.150.146', '加拿大', '蒙特利尔', '', 1, '[]', '1767296458');
INSERT INTO `visitinfo` VALUES ('1767516089179', '27.154.105.127', '中国', '厦门市', '福建省', 1, '[]', '1767516089');
INSERT INTO `visitinfo` VALUES ('1767547380621', '122.192.32.169', '中国', '南京市', '江苏省', 1, '[]', '1767547380');
INSERT INTO `visitinfo` VALUES ('1767584137680', '183.38.217.147', '中国', '深圳市', '广东省', 1, '[]', '1767584148');
INSERT INTO `visitinfo` VALUES ('1767620502693', '123.113.205.245', '中国', '北京市', '北京市', 1, '[]', '1767620503');
INSERT INTO `visitinfo` VALUES ('1767629000622', '113.141.86.82', '中国', '西安市', '陕西省', 1, '[]', '1767629000');
INSERT INTO `visitinfo` VALUES ('1767667816306', '74.7.227.35', '美国', '亚特兰大', '', 1, '[]', '1767667816');
INSERT INTO `visitinfo` VALUES ('1767804733343', '3.95.164.5', '美国', 'Ashburn', '', 1, '[]', '1767804733');
INSERT INTO `visitinfo` VALUES ('1767809841423', '180.153.236.155', '中国', '上海市', '上海市', 1, '[]', '1767809841');
INSERT INTO `visitinfo` VALUES ('1767822124093', '91.98.178.79', '德国', 'Falkenstein', '', 1, '[]', '1767822124');
INSERT INTO `visitinfo` VALUES ('1768037821928', '124.127.28.57', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1768037842\",\"platform\":\"Android\"}]', '1768037833');
INSERT INTO `visitinfo` VALUES ('1768037826358', '61.159.16.232', '中国', '保定市', '河北省', 1, '[{\"downCount\":1,\"downTime\":\"1768037869\",\"platform\":\"Android\"}]', '1768037858');
INSERT INTO `visitinfo` VALUES ('1768037898590', '1.202.124.57', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1768037916\",\"platform\":\"Android\"}]', '1768037903');
INSERT INTO `visitinfo` VALUES ('1768064068542', '124.127.70.140', '中国', '北京市', '北京市', 1, '[]', '1768064068');
INSERT INTO `visitinfo` VALUES ('1768108351241', '180.76.28.44', '中国', '北京市', '北京市', 1, '[]', '1768108351');
INSERT INTO `visitinfo` VALUES ('1768108506506', '205.169.39.128', '美国', '圣克拉拉', '', 1, '[]', '1768108506');
INSERT INTO `visitinfo` VALUES ('1768108580841', '205.169.39.4', '美国', '圣克拉拉', '', 1, '[]', '1768108580');
INSERT INTO `visitinfo` VALUES ('1768109948600', '183.254.112.238', '中国', '海口市', '海南省', 1, '[]', '1768109948');
INSERT INTO `visitinfo` VALUES ('1768168938056', '34.133.237.224', '美国', '康瑟尔布拉夫斯', '', 1, '[]', '1768168938');
INSERT INTO `visitinfo` VALUES ('1768322893479', '111.193.13.111', '中国', '北京市', '北京市', 1, '[]', '1768322893');
INSERT INTO `visitinfo` VALUES ('1768392331194', '111.172.6.153', '中国', '武汉市', '湖北省', 1, '[]', '1768392331');
INSERT INTO `visitinfo` VALUES ('1768469679365', '180.153.236.110', '中国', '上海市', '上海市', 1, '[]', '1780156719');
INSERT INTO `visitinfo` VALUES ('1768479320518', '114.250.241.38', '中国', '北京市', '北京市', 1, '[]', '1769226381');
INSERT INTO `visitinfo` VALUES ('1768498292828', '180.153.236.196', '中国', '上海市', '上海市', 1, '[]', '1768498292');
INSERT INTO `visitinfo` VALUES ('1768551116478', '66.249.72.64', '美国', '三河市', '', 1, '[]', '1780277266');
INSERT INTO `visitinfo` VALUES ('1768761846734', '113.141.84.45', '中国', '西安市', '陕西省', 1, '[]', '1768761846');
INSERT INTO `visitinfo` VALUES ('1768761932377', '180.153.236.80', '中国', '上海市', '上海市', 1, '[]', '1768761932');
INSERT INTO `visitinfo` VALUES ('1768762063472', '113.141.84.72', '中国', '西安市', '陕西省', 1, '[]', '1768762063');
INSERT INTO `visitinfo` VALUES ('1769026329110', '220.196.160.154', '中国', '常州市', '江苏省', 1, '[]', '1770837741');
INSERT INTO `visitinfo` VALUES ('1769096099798', '113.141.81.236', '中国', '西安市', '陕西省', 1, '[]', '1769096099');
INSERT INTO `visitinfo` VALUES ('1769125988668', '180.153.236.47', '中国', '上海市', '上海市', 1, '[]', '1777490548');
INSERT INTO `visitinfo` VALUES ('1769234188952', '125.122.12.195', '中国', '杭州市', '浙江省', 1, '[]', '1769234188');
INSERT INTO `visitinfo` VALUES ('1769248106984', '49.87.80.160', '中国', '淮安市', '江苏省', 1, '[]', '1769248106');
INSERT INTO `visitinfo` VALUES ('1769375978121', '180.153.236.224', '中国', '上海市', '上海市', 1, '[]', '1769375978');
INSERT INTO `visitinfo` VALUES ('1769505265524', '59.83.208.107', '中国', '济南市', '山东省', 1, '[]', '1769505265');
INSERT INTO `visitinfo` VALUES ('1769518233201', '221.223.13.109', '中国', '北京市', '北京市', 1, '[]', '1769518240');
INSERT INTO `visitinfo` VALUES ('1769559567842', '125.122.15.83', '中国', '杭州市', '浙江省', 1, '[]', '1769559567');
INSERT INTO `visitinfo` VALUES ('1769576167978', '66.249.72.66', '美国', '三河市', '', 1, '[]', '1777418049');
INSERT INTO `visitinfo` VALUES ('1769745653500', '40.77.178.212', '美国', '昆西', '', 1, '[]', '1769745653');
INSERT INTO `visitinfo` VALUES ('1769800507856', '42.236.17.224', '中国', '郑州市', '河南省', 1, '[]', '1769800507');
INSERT INTO `visitinfo` VALUES ('1769825712264', '66.249.72.65', '美国', '三河市', '', 1, '[]', '1777694643');
INSERT INTO `visitinfo` VALUES ('1769926817854', '149.56.160.151', '加拿大', '蒙特利尔', '', 1, '[]', '1769926817');
INSERT INTO `visitinfo` VALUES ('1769963967570', '205.169.39.53', '美国', '圣克拉拉', '', 1, '[]', '1769963967');
INSERT INTO `visitinfo` VALUES ('1770278083836', '49.7.4.33', '中国', '北京市', '北京市', 1, '[]', '1770278083');
INSERT INTO `visitinfo` VALUES ('1770278083869', '120.207.99.241', '中国', '阳泉市', '山西省', 1, '[{\"downCount\":1,\"downTime\":\"1770278084\",\"platform\":\"Android\"}]', '1770278083');
INSERT INTO `visitinfo` VALUES ('1770358030459', '180.153.236.121', '中国', '上海市', '上海市', 1, '[]', '1770358030');
INSERT INTO `visitinfo` VALUES ('1770418228557', '125.122.32.66', '中国', '杭州市', '浙江省', 1, '[]', '1770418228');
INSERT INTO `visitinfo` VALUES ('1770570490794', '180.153.236.122', '中国', '上海市', '上海市', 1, '[]', '1770570490');
INSERT INTO `visitinfo` VALUES ('1770724946569', '222.128.65.79', '中国', '北京市', '北京市', 1, '[]', '1770724946');
INSERT INTO `visitinfo` VALUES ('1770775642605', '123.117.171.99', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1773054519\",\"platform\":\"MacOS\"}]', '1775628556');
INSERT INTO `visitinfo` VALUES ('1770821450710', '154.31.112.242', '日本', '东京', '', 1, '[]', '1770821450');
INSERT INTO `visitinfo` VALUES ('1770884986228', '223.160.122.194', '中国', '北京市', '北京市', 1, '[]', '1770885039');
INSERT INTO `visitinfo` VALUES ('1770906745117', '221.221.15.41', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1772975588\",\"platform\":\"MacOS\"}]', '1776818913');
INSERT INTO `visitinfo` VALUES ('1770916079725', '180.153.236.154', '中国', '上海市', '上海市', 1, '[]', '1770916079');
INSERT INTO `visitinfo` VALUES ('1770931280645', '34.72.176.129', '美国', '康瑟尔布拉夫斯', '', 1, '[]', '1770931280');
INSERT INTO `visitinfo` VALUES ('1770931358809', '205.169.39.13', '美国', '圣克拉拉', '', 1, '[]', '1770931358');
INSERT INTO `visitinfo` VALUES ('1771028547770', '125.44.184.18', '中国', '安阳市', '河南省', 1, '[]', '1771028547');
INSERT INTO `visitinfo` VALUES ('1771054550724', '122.192.32.86', '中国', '南京市', '江苏省', 1, '[]', '1771054550');
INSERT INTO `visitinfo` VALUES ('1771064658031', '60.205.166.165', '中国', '北京市', '北京市', 1, '[]', '1771064658');
INSERT INTO `visitinfo` VALUES ('1771235780206', '171.113.59.97', '中国', '武汉市', '湖北省', 1, '[]', '1771235780');
INSERT INTO `visitinfo` VALUES ('1771245434522', '205.169.39.187', '美国', '圣克拉拉', '', 1, '[]', '1771245434');
INSERT INTO `visitinfo` VALUES ('1771248718830', '40.77.179.202', '美国', '昆西', '', 1, '[]', '1771248718');
INSERT INTO `visitinfo` VALUES ('1771270199993', '180.153.236.232', '中国', '上海市', '上海市', 1, '[]', '1771270199');
INSERT INTO `visitinfo` VALUES ('1771289812154', '180.153.236.124', '中国', '上海市', '上海市', 1, '[]', '1771289812');
INSERT INTO `visitinfo` VALUES ('1771464414997', '114.224.105.57', '中国', '无锡市', '江苏省', 1, '[]', '1771464414');
INSERT INTO `visitinfo` VALUES ('1771506738631', '117.157.89.186', '中国', '兰州市', '甘肃省', 1, '[{\"downCount\":1,\"downTime\":\"1771506740\",\"platform\":\"Android\"}]', '1771506738');
INSERT INTO `visitinfo` VALUES ('1771553694911', '117.157.97.69', '中国', '兰州市', '甘肃省', 1, '[]', '1771553694');
INSERT INTO `visitinfo` VALUES ('1771580198261', '113.141.80.133', '中国', '西安市', '陕西省', 1, '[]', '1771580198');
INSERT INTO `visitinfo` VALUES ('1771657379416', '153.35.209.119', '中国', '南京市', '江苏省', 1, '[]', '1771657379');
INSERT INTO `visitinfo` VALUES ('1771730737783', '18.119.128.13', '美国', 'Dublin', '', 1, '[]', '1771730737');
INSERT INTO `visitinfo` VALUES ('1771733062723', '52.15.144.221', '美国', 'Dublin', '', 1, '[]', '1771733062');
INSERT INTO `visitinfo` VALUES ('1771743627970', '66.249.72.2', '美国', '芒廷维尤', '', 1, '[]', '1780438738');
INSERT INTO `visitinfo` VALUES ('1771793032712', '125.122.33.89', '中国', '杭州市', '浙江省', 1, '[]', '1771793032');
INSERT INTO `visitinfo` VALUES ('1771798201486', '180.153.236.157', '中国', '上海市', '上海市', 1, '[]', '1771798201');
INSERT INTO `visitinfo` VALUES ('1771935694938', '13.221.197.209', '美国', 'Ashburn', '', 1, '[]', '1771935694');
INSERT INTO `visitinfo` VALUES ('1771936416395', '35.173.42.173', '美国', 'Ashburn', '', 1, '[]', '1771936416');
INSERT INTO `visitinfo` VALUES ('1771938027366', '44.200.49.104', '美国', 'Ashburn', '', 1, '[]', '1771938027');
INSERT INTO `visitinfo` VALUES ('1772070928969', '66.249.72.1', '美国', '芒廷维尤', '', 1, '[]', '1780438737');
INSERT INTO `visitinfo` VALUES ('1772128337217', '180.153.236.57', '中国', '上海市', '上海市', 1, '[]', '1772128337');
INSERT INTO `visitinfo` VALUES ('1772140643253', '121.57.253.14', '中国', '赤峰市', '内蒙古自治区', 1, '[]', '1772140643');
INSERT INTO `visitinfo` VALUES ('1772140643901', '52.80.102.53', '中国', '北京市', '北京市', 1, '[]', '1772140643');
INSERT INTO `visitinfo` VALUES ('1772140644733', '54.223.179.59', '中国', '北京市', '北京市', 1, '[]', '1772140644');
INSERT INTO `visitinfo` VALUES ('1772140658118', '54.223.43.186', '中国', '北京市', '北京市', 1, '[]', '1772140658');
INSERT INTO `visitinfo` VALUES ('1772170769275', '123.57.206.123', '中国', '北京市', '北京市', 1, '[]', '1772170769');
INSERT INTO `visitinfo` VALUES ('1772265621273', '49.65.18.13', '中国', '南京市', '江苏省', 1, '[]', '1772265621');
INSERT INTO `visitinfo` VALUES ('1772345812654', '149.56.150.20', '加拿大', '蒙特利尔', '', 1, '[]', '1772345812');
INSERT INTO `visitinfo` VALUES ('1772491008379', '180.153.236.46', '中国', '上海市', '上海市', 1, '[]', '1772491008');
INSERT INTO `visitinfo` VALUES ('1772645139492', '220.196.160.45', '中国', '常州市', '江苏省', 1, '[]', '1776092048');
INSERT INTO `visitinfo` VALUES ('1772687399345', '171.107.242.213', '中国', '南宁市', '广西壮族自治区', 1, '[]', '1772687399');
INSERT INTO `visitinfo` VALUES ('1772731354725', '180.153.236.237', '中国', '上海市', '上海市', 1, '[]', '1772731354');
INSERT INTO `visitinfo` VALUES ('1772818087184', '54.223.94.218', '中国', '北京市', '北京市', 1, '[]', '1772818087');
INSERT INTO `visitinfo` VALUES ('1772818134670', '54.223.134.56', '中国', '北京市', '北京市', 1, '[]', '1772818134');
INSERT INTO `visitinfo` VALUES ('1772893031293', '111.172.6.158', '中国', '武汉市', '湖北省', 1, '[]', '1772893031');
INSERT INTO `visitinfo` VALUES ('1772918650588', '114.245.32.53', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1772918654\",\"platform\":\"Android\"}]', '1772918650');
INSERT INTO `visitinfo` VALUES ('1772931847643', '183.254.111.135', '中国', '海口市', '海南省', 1, '[{\"downCount\":1,\"downTime\":\"1772934827\",\"platform\":\"Windows\"}]', '1772938672');
INSERT INTO `visitinfo` VALUES ('1773060135064', '180.153.236.194', '中国', '上海市', '上海市', 1, '[]', '1773060135');
INSERT INTO `visitinfo` VALUES ('1773090753719', '113.141.84.149', '中国', '西安市', '陕西省', 1, '[]', '1773090753');
INSERT INTO `visitinfo` VALUES ('1773125664635', '223.97.193.62', '中国', '潍坊市', '山东省', 1, '[{\"downCount\":2,\"downTime\":\"1773125838\",\"platform\":\"Windows\"}]', '1773126154');
INSERT INTO `visitinfo` VALUES ('1773126559231', '112.244.106.41', '中国', '潍坊市', '山东省', 1, '[{\"downCount\":2,\"downTime\":\"1773126575\",\"platform\":\"Android\"}]', '1773126575');
INSERT INTO `visitinfo` VALUES ('1773128745039', '124.223.51.164', '中国', '北京市', '北京市', 1, '[]', '1773541159');
INSERT INTO `visitinfo` VALUES ('1773137452621', '118.89.49.150', '中国', '天津市', '天津市', 1, '[]', '1773137452');
INSERT INTO `visitinfo` VALUES ('1773142141180', '42.81.251.36', '中国', '天津市', '天津市', 1, '[]', '1773142141');
INSERT INTO `visitinfo` VALUES ('1773199318195', '66.249.72.3', '美国', '芒廷维尤', '', 1, '[]', '1773199318');
INSERT INTO `visitinfo` VALUES ('1773232950529', '183.254.110.152', '中国', '海口市', '海南省', 1, '[{\"downCount\":3,\"downTime\":\"1773233791\",\"platform\":\"Windows\"}]', '1773235214');
INSERT INTO `visitinfo` VALUES ('1773347587258', '60.216.105.162', '中国', '济南市', '山东省', 1, '[]', '1773347587');
INSERT INTO `visitinfo` VALUES ('1773420049834', '44.210.125.185', '美国', 'Ashburn', '', 1, '[]', '1773420049');
INSERT INTO `visitinfo` VALUES ('1773420151609', '100.53.107.98', '美国', 'Ashburn', '', 1, '[]', '1773420151');
INSERT INTO `visitinfo` VALUES ('1773476635082', '122.192.32.130', '中国', '南京市', '江苏省', 1, '[]', '1773476635');
INSERT INTO `visitinfo` VALUES ('1773523966188', '180.153.236.76', '中国', '上海市', '上海市', 1, '[]', '1773523966');
INSERT INTO `visitinfo` VALUES ('1773537032676', '171.218.211.220', '中国', '成都市', '四川省', 1, '[{\"downCount\":1,\"downTime\":\"1773537049\",\"platform\":\"Android\"}]', '1773537032');
INSERT INTO `visitinfo` VALUES ('1773648754701', '180.153.236.44', '中国', '上海市', '上海市', 1, '[]', '1773648754');
INSERT INTO `visitinfo` VALUES ('1773683044516', '125.122.15.57', '中国', '杭州市', '浙江省', 1, '[]', '1773683044');
INSERT INTO `visitinfo` VALUES ('1773698170493', '40.77.178.238', '美国', '昆西', '', 1, '[]', '1773698170');
INSERT INTO `visitinfo` VALUES ('1773724229065', '153.3.233.104', '中国', '南京市', '江苏省', 1, '[]', '1773724229');
INSERT INTO `visitinfo` VALUES ('1773825418693', '112.244.61.117', '中国', '潍坊市', '山东省', 1, '[{\"downCount\":2,\"downTime\":\"1773836481\",\"platform\":\"Windows\"}]', '1773825418');
INSERT INTO `visitinfo` VALUES ('1773916407585', '223.97.184.148', '中国', '潍坊市', '山东省', 1, '[]', '1773916407');
INSERT INTO `visitinfo` VALUES ('1773916424552', '54.223.103.137', '中国', '北京市', '北京市', 1, '[]', '1775031162');
INSERT INTO `visitinfo` VALUES ('1773916446973', '54.223.131.56', '中国', '北京市', '北京市', 1, '[]', '1773916446');
INSERT INTO `visitinfo` VALUES ('1773936760919', '180.153.236.67', '中国', '上海市', '上海市', 1, '[]', '1773936760');
INSERT INTO `visitinfo` VALUES ('1774077401769', '122.192.32.98', '中国', '南京市', '江苏省', 1, '[]', '1774077401');
INSERT INTO `visitinfo` VALUES ('1774230402153', '180.153.236.62', '中国', '上海市', '上海市', 1, '[]', '1774230402');
INSERT INTO `visitinfo` VALUES ('1774271201685', '111.172.6.207', '中国', '武汉市', '湖北省', 1, '[]', '1774271201');
INSERT INTO `visitinfo` VALUES ('1774408675095', '58.247.202.164', '中国', '上海市', '上海市', 1, '[]', '1774408675');
INSERT INTO `visitinfo` VALUES ('1774456136752', '149.88.103.92', '日本', '东京', '', 1, '[]', '1774456136');
INSERT INTO `visitinfo` VALUES ('1774740883973', '180.153.236.53', '中国', '上海市', '上海市', 1, '[]', '1774740883');
INSERT INTO `visitinfo` VALUES ('1774823895244', '180.153.236.163', '中国', '上海市', '上海市', 1, '[]', '1774823895');
INSERT INTO `visitinfo` VALUES ('1774823899172', '180.153.236.112', '中国', '上海市', '上海市', 1, '[]', '1774823899');
INSERT INTO `visitinfo` VALUES ('1775008741469', '66.249.72.67', '美国', '芒廷维尤', '', 1, '[]', '1775008741');
INSERT INTO `visitinfo` VALUES ('1775021186894', '149.56.150.189', '加拿大', '蒙特利尔', '', 1, '[]', '1775021186');
INSERT INTO `visitinfo` VALUES ('1775031165830', '54.223.96.178', '中国', '北京市', '北京市', 1, '[]', '1775031165');
INSERT INTO `visitinfo` VALUES ('1775031174427', '52.80.79.184', '中国', '北京市', '北京市', 1, '[]', '1775031174');
INSERT INTO `visitinfo` VALUES ('1775031202535', '39.91.71.70', '中国', '济南市', '山东省', 1, '[]', '1775031202');
INSERT INTO `visitinfo` VALUES ('1775186970501', '180.153.236.105', '中国', '上海市', '上海市', 1, '[]', '1775186970');
INSERT INTO `visitinfo` VALUES ('1775230926198', '44.221.82.4', '美国', 'Ashburn', '', 1, '[]', '1775230926');
INSERT INTO `visitinfo` VALUES ('1775336707196', '42.193.132.4', '中国', '上海市', '上海市', 1, '[]', '1780174460');
INSERT INTO `visitinfo` VALUES ('1775376240803', '223.101.87.67', '中国', '沈阳市', '辽宁省', 1, '[{\"downCount\":1,\"downTime\":\"1775376242\",\"platform\":\"Android\"}]', '1775376240');
INSERT INTO `visitinfo` VALUES ('1775376242883', '54.223.34.106', '中国', '北京市', '北京市', 1, '[]', '1775376242');
INSERT INTO `visitinfo` VALUES ('1775376252503', '52.80.47.64', '中国', '北京市', '北京市', 1, '[]', '1775376252');
INSERT INTO `visitinfo` VALUES ('1775405067477', '153.35.209.87', '中国', '南京市', '江苏省', 1, '[]', '1775405067');
INSERT INTO `visitinfo` VALUES ('1775573389959', '183.254.112.170', '中国', '海口市', '海南省', 1, '[]', '1775573389');
INSERT INTO `visitinfo` VALUES ('1775573396625', '154.31.112.236', '日本', '东京', '', 1, '[]', '1776698866');
INSERT INTO `visitinfo` VALUES ('1775628554063', '42.88.144.63', '中国', '酒泉市', '甘肃省', 1, '[{\"downCount\":5,\"downTime\":\"1775630619\",\"platform\":\"Android\"}]', '1775630599');
INSERT INTO `visitinfo` VALUES ('1775696766529', '17.88.152.162', '日本', '東京都', '', 1, '[]', '1775696766');
INSERT INTO `visitinfo` VALUES ('1775830334944', '180.153.236.180', '中国', '上海市', '上海市', 1, '[]', '1775830334');
INSERT INTO `visitinfo` VALUES ('1775886997060', '111.29.131.21', '中国', '海口市', '海南省', 1, '[]', '1775886997');
INSERT INTO `visitinfo` VALUES ('1776008904455', '40.77.178.160', '美国', '昆西', '', 1, '[]', '1776008904');
INSERT INTO `visitinfo` VALUES ('1776018330784', '122.192.32.151', '中国', '南京市', '江苏省', 1, '[]', '1776018330');
INSERT INTO `visitinfo` VALUES ('1776032309368', '66.249.72.68', '美国', '芒廷维尤', '', 1, '[]', '1776032309');
INSERT INTO `visitinfo` VALUES ('1776044118360', '221.216.133.236', '中国', '北京市', '北京市', 1, '[]', '1780628913');
INSERT INTO `visitinfo` VALUES ('1776069350264', '123.127.230.206', '中国', '北京市', '北京市', 1, '[{\"downCount\":1,\"downTime\":\"1776069377\",\"platform\":\"Android\"}]', '1776069364');
INSERT INTO `visitinfo` VALUES ('1776474759353', '123.56.163.203', '中国', '北京市', '北京市', 1, '[]', '1776474759');
INSERT INTO `visitinfo` VALUES ('1776490553323', '3.229.118.5', '美国', 'Ashburn', '', 1, '[]', '1776490553');
INSERT INTO `visitinfo` VALUES ('1776497569545', '60.205.124.68', '中国', '北京市', '北京市', 1, '[]', '1776497569');
INSERT INTO `visitinfo` VALUES ('1776591033410', '111.29.176.147', '中国', '海口市', '海南省', 1, '[]', '1776698583');
INSERT INTO `visitinfo` VALUES ('1776805524593', '42.193.132.157', '中国', '上海市', '上海市', 1, '[]', '1776805524');
INSERT INTO `visitinfo` VALUES ('1776855325089', '122.192.32.175', '中国', '南京市', '江苏省', 1, '[]', '1776855325');
INSERT INTO `visitinfo` VALUES ('1776905078812', '119.3.119.8', '中華人民共和國', '中国上海', '', 1, '[]', '1776905078');
INSERT INTO `visitinfo` VALUES ('1776947327363', '125.122.33.78', '中国', '杭州市', '浙江省', 1, '[]', '1776947327');
INSERT INTO `visitinfo` VALUES ('1777037862507', '180.153.236.131', '中国', '上海市', '上海市', 1, '[]', '1777037862');
INSERT INTO `visitinfo` VALUES ('1777042146040', '182.34.51.24', '中国', '烟台市', '山东省', 1, '[{\"downCount\":1,\"downTime\":\"1777042151\",\"platform\":\"Android\"}]', '1777042146');
INSERT INTO `visitinfo` VALUES ('1777064798724', '180.153.236.116', '中国', '上海市', '上海市', 1, '[]', '1777064798');
INSERT INTO `visitinfo` VALUES ('1777083402292', '119.3.119.14', '中華人民共和國', '中国上海', '', 1, '[{\"downCount\":4,\"downTime\":\"1777347065\",\"platform\":\"Android\"}]', '1777347064');
INSERT INTO `visitinfo` VALUES ('1777139412266', '180.153.236.52', '中国', '上海市', '上海市', 1, '[]', '1777139412');
INSERT INTO `visitinfo` VALUES ('1777168504650', '180.153.236.45', '中国', '上海市', '上海市', 1, '[]', '1777168504');
INSERT INTO `visitinfo` VALUES ('1777272419823', '180.153.236.49', '中国', '上海市', '上海市', 1, '[]', '1777272419');
INSERT INTO `visitinfo` VALUES ('1777285705448', '180.153.236.179', '中国', '上海市', '上海市', 1, '[]', '1780582097');
INSERT INTO `visitinfo` VALUES ('1777297619275', '49.65.18.3', '中国', '南京市', '江苏省', 1, '[]', '1777297619');
INSERT INTO `visitinfo` VALUES ('1777320484490', '113.141.80.245', '中国', '西安市', '陕西省', 1, '[]', '1777320484');
INSERT INTO `visitinfo` VALUES ('1777366163414', '180.153.236.144', '中国', '上海市', '上海市', 1, '[]', '1777366163');
INSERT INTO `visitinfo` VALUES ('1777379796661', '113.141.86.87', '中国', '西安市', '陕西省', 1, '[]', '1777379796');
INSERT INTO `visitinfo` VALUES ('1777427108610', '111.28.74.250', '中国', '海口市', '海南省', 1, '[{\"downCount\":3,\"downTime\":\"1777428227\",\"platform\":\"Android\"}]', '1777428224');
INSERT INTO `visitinfo` VALUES ('1777427211962', '116.30.196.195', '中国', '深圳市', '广东省', 1, '[]', '1777427211');
INSERT INTO `visitinfo` VALUES ('1777541284088', '180.153.236.227', '中国', '上海市', '上海市', 1, '[]', '1777541284');
INSERT INTO `visitinfo` VALUES ('1777617614894', '123.6.49.36', '中国', '郑州市', '河南省', 1, '[]', '1777617614');
INSERT INTO `visitinfo` VALUES ('1777645224897', '149.56.160.189', '加拿大', '蒙特利尔', '', 1, '[]', '1777645224');
INSERT INTO `visitinfo` VALUES ('1779188796782', '180.153.236.36', '中国', '上海市', '上海市', 1, '[]', '1780627504');
INSERT INTO `visitinfo` VALUES ('1779240591446', '180.153.236.25', '中国', '上海市', '上海市', 1, '[]', '1779240591');
INSERT INTO `visitinfo` VALUES ('1779323726436', '180.153.236.22', '中国', '上海市', '上海市', 1, '[]', '1779323726');
INSERT INTO `visitinfo` VALUES ('1779370678380', '180.153.236.6', '中国', '上海市', '上海市', 1, '[]', '1779370678');
INSERT INTO `visitinfo` VALUES ('1779372648690', '183.254.111.81', '中国', '海口市', '海南省', 1, '[{\"downCount\":1,\"downTime\":\"1779372653\",\"platform\":\"Android\"}]', '1779372648');
INSERT INTO `visitinfo` VALUES ('1779377922394', '123.113.207.194', '中国', '北京市', '北京市', 1, '[]', '1779377931');
INSERT INTO `visitinfo` VALUES ('1779388510971', '202.8.43.36', '美国', 'Sterling', '', 1, '[]', '1779388510');
INSERT INTO `visitinfo` VALUES ('1779523113740', '180.153.236.203', '中国', '上海市', '上海市', 1, '[]', '1779523113');
INSERT INTO `visitinfo` VALUES ('1779528859433', '122.192.32.103', '中国', '南京市', '江苏省', 1, '[]', '1779528859');
INSERT INTO `visitinfo` VALUES ('1779648530667', '180.153.236.219', '中国', '上海市', '上海市', 1, '[]', '1779648530');
INSERT INTO `visitinfo` VALUES ('1779721084569', '180.153.236.228', '中国', '上海市', '上海市', 1, '[]', '1779721084');
INSERT INTO `visitinfo` VALUES ('1779723400264', '180.153.236.41', '中国', '上海市', '上海市', 1, '[]', '1779723400');
INSERT INTO `visitinfo` VALUES ('1779775982025', '180.153.236.188', '中国', '上海市', '上海市', 1, '[]', '1779775982');
INSERT INTO `visitinfo` VALUES ('1779775986027', '180.153.236.252', '中国', '上海市', '上海市', 1, '[]', '1779775986');
INSERT INTO `visitinfo` VALUES ('1779894986925', '180.153.236.81', '中国', '上海市', '上海市', 1, '[]', '1779894986');
INSERT INTO `visitinfo` VALUES ('1779920610666', '180.153.236.17', '中国', '上海市', '上海市', 1, '[]', '1779920610');
INSERT INTO `visitinfo` VALUES ('1780043237616', '180.153.236.216', '中国', '上海市', '上海市', 1, '[]', '1780043237');
INSERT INTO `visitinfo` VALUES ('1780063675068', '180.153.236.58', '中国', '上海市', '上海市', 1, '[]', '1780063675');
INSERT INTO `visitinfo` VALUES ('1780151757731', '120.217.169.255', '中国', '濮阳市', '河南省', 1, '[]', '1780151777');
INSERT INTO `visitinfo` VALUES ('1780231998163', '111.29.131.76', '中国', '海口市', '海南省', 1, '[{\"downCount\":2,\"downTime\":\"1780233237\",\"platform\":\"Android\"}]', '1780298627');
INSERT INTO `visitinfo` VALUES ('1780239670160', '180.153.236.42', '中国', '上海市', '上海市', 1, '[]', '1780239670');
INSERT INTO `visitinfo` VALUES ('1780246278084', '180.153.236.183', '中国', '上海市', '上海市', 1, '[]', '1780246278');
INSERT INTO `visitinfo` VALUES ('1780287854031', '124.133.146.119', '中国', '济南市', '山东省', 1, '[{\"downCount\":1,\"downTime\":\"1780287862\",\"platform\":\"Android\"}]', '1780287854');
INSERT INTO `visitinfo` VALUES ('1780291429822', '144.217.135.196', '加拿大', 'Beauharnois', '', 1, '[]', '1780291429');
INSERT INTO `visitinfo` VALUES ('1780307117173', '123.118.20.131', '中国', '北京市', '北京市', 1, '[]', '1780307117');
INSERT INTO `visitinfo` VALUES ('1780346415493', '180.153.236.123', '中国', '上海市', '上海市', 1, '[]', '1780346415');
INSERT INTO `visitinfo` VALUES ('1780362552774', '110.249.201.83', '中国', '石家庄市', '河北省', 1, '[]', '1780362552');
INSERT INTO `visitinfo` VALUES ('1780436981930', '180.153.236.109', '中国', '上海市', '上海市', 1, '[]', '1780436981');
INSERT INTO `visitinfo` VALUES ('1780469152311', '183.207.45.120', '中国', '南京市', '江苏省', 1, '[]', '1780469152');
INSERT INTO `visitinfo` VALUES ('1780516858597', '180.153.236.79', '中国', '上海市', '上海市', 1, '[]', '1780516858');
INSERT INTO `visitinfo` VALUES ('1780527361805', '180.153.236.176', '中国', '上海市', '上海市', 1, '[]', '1780527361');
INSERT INTO `visitinfo` VALUES ('1780557714202', '36.57.134.186', '中国', '合肥市', '安徽省', 1, '[]', '1780557714');
INSERT INTO `visitinfo` VALUES ('1780606491160', '113.141.85.104', '中国', '西安市', '陕西省', 1, '[]', '1780606491');
INSERT INTO `visitinfo` VALUES ('1780651337660', '180.153.236.134', '中国', '上海市', '上海市', 1, '[]', '1780651337');
INSERT INTO `visitinfo` VALUES ('1780667337484', '180.153.236.32', '中国', '上海市', '上海市', 1, '[]', '1780667337');

SET FOREIGN_KEY_CHECKS = 1;
