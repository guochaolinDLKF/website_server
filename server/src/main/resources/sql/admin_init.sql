-- ============================================================
-- 运营后台 建表脚本（website / operations 库）
-- 适用：MySQL 8.0 / utf8mb4
-- 说明：仅建后台自身数据表，不涉及 zhouyi(APP 业务库)。
--       超级管理员账号(admin)及其密码由应用启动时的 AdminDataInitializer
--       以 BCrypt 安全写入，本脚本不存放明文/弱哈希密码。
-- 执行：在 operations 库执行一次即可（可重复执行，使用 IF NOT EXISTS / INSERT IGNORE）。
-- ============================================================

-- 1. 后台管理员 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删 1已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员';

-- 2. 角色 -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码 SUPER_ADMIN/OPERATOR...',
  `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台角色';

-- 3. 权限/菜单 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限码 user:list 等',
  `permission_name` VARCHAR(64) NOT NULL COMMENT '名称',
  `permission_type` TINYINT NOT NULL COMMENT '1菜单 2按钮 3接口',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '前端路由/接口路径',
  `component` VARCHAR(200) DEFAULT NULL COMMENT '前端组件',
  `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台权限/菜单';

-- 4. 角色-权限 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_perm` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 5. 管理员-角色 -----------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `admin_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`),
  KEY `idx_admin` (`admin_id`),
  KEY `idx_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联';

-- 6. 登录日志 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `admin_id` BIGINT DEFAULT NULL,
  `username` VARCHAR(64) DEFAULT NULL,
  `login_ip` VARCHAR(64) DEFAULT NULL,
  `user_agent` VARCHAR(512) DEFAULT NULL,
  `login_type` TINYINT NOT NULL DEFAULT 1 COMMENT '1登录 2登出',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1成功 0失败',
  `msg` VARCHAR(255) DEFAULT NULL,
  `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin` (`admin_id`),
  KEY `idx_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台登录日志';

-- 7. 操作日志 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `admin_id` BIGINT DEFAULT NULL,
  `username` VARCHAR(64) DEFAULT NULL,
  `module` VARCHAR(64) DEFAULT NULL,
  `operation` VARCHAR(128) DEFAULT NULL,
  `method` VARCHAR(200) DEFAULT NULL COMMENT '类.方法',
  `request_uri` VARCHAR(255) DEFAULT NULL,
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT 'GET/POST...',
  `request_param` MEDIUMTEXT,
  `response_result` MEDIUMTEXT,
  `ip` VARCHAR(64) DEFAULT NULL,
  `cost_ms` BIGINT DEFAULT NULL COMMENT '耗时(毫秒)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1成功 0异常',
  `error_msg` TEXT,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin` (`admin_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志';

-- 8. 系统配置 -------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_name` VARCHAR(100) DEFAULT NULL COMMENT '配置名称',
  `config_group` VARCHAR(64) DEFAULT NULL COMMENT '分组',
  `remark` VARCHAR(255) DEFAULT NULL,
  `update_by` VARCHAR(64) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- ============================================================
-- 初始化数据：角色 + 权限菜单树（非敏感数据）
-- ============================================================

-- 角色（5 个预设角色）
INSERT IGNORE INTO `admin_role` (`id`,`role_code`,`role_name`,`description`,`sort_order`) VALUES
 (1,'SUPER_ADMIN','超级管理员','拥有全部权限',1),
 (2,'OPERATOR','运营','运营人员，内容/功能/活动编辑，数据查看',2),
 (3,'SERVICE','客服','用户查看与禁用，订单查看',3),
 (4,'FINANCE','财务','订单/退款/收入相关',4),
 (5,'ANALYST','数据分析','数据分析查看',5),
 (6,'ADMIN','管理员','系统管理员，可管理商品等业务数据（仅次于超级管理员）',6);

-- 权限/菜单树
INSERT IGNORE INTO `admin_permission` (`id`,`permission_code`,`permission_name`,`permission_type`,`parent_id`,`path`,`icon`,`sort_order`) VALUES
 -- 一级菜单
 (1 ,'dashboard:view','主控制台',1,0 ,'/dashboard','DataLine',1),
 (10,'user:menu'      ,'用户中心'  ,1,0 ,'/user','User',2),
 (20,'order:menu'     ,'商城管理',1,0 ,'/order','Goods',3),
 (35,'analytics:menu' ,'数据分析'  ,1,0 ,'/analytics','TrendCharts',5),
 (40,'system:menu'    ,'系统管理'  ,1,0 ,'/system','Setting',6),
 -- 用户中心
 (11,'user:list'  ,'用户列表',1,10,'/user/list','',1),
 (12,'user:detail','查看详情',2,11,'','',1),
 (13,'user:disable','启用/禁用',2,11,'','',2),
 (14,'user:export','导出',2,11,'','',3),
 -- 商城管理
 (21,'order:list'  ,'订单管理',1,20,'/order/list','',1),
 (22,'order:detail','订单详情',2,21,'','',1),
 (23,'order:refund','退款操作',2,21,'','',2),
 (24,'order:export','导出',2,21,'','',3),
 (25,'goods:list'  ,'商品管理',1,20,'/order/goods','',2),
 (26,'goods:edit'  ,'编辑商品',2,25,'','',1),
 (28,'benefit:list','权益管理',1,20,'/order/benefit','',4),
 (29,'payfail:list','支付异常监控',1,20,'/order/payment-failure','',5),
 -- 数据分析
 (39,'analytics:realtime','实时数据',1,35,'/analytics/realtime','',1),
 (50,'analytics:newdata' ,'新增数据',1,35,'/analytics/newdata','',2),
 (51,'analytics:payanalysis','付费分析',1,35,'/analytics/payment','',3),
 -- 系统管理
 (41,'admin:list'     ,'管理员管理',1,40,'/system/admin','',1),
 (42,'admin:edit'     ,'编辑管理员',2,41,'','',1),
 (43,'role:list'      ,'角色管理',1,40,'/system/role','',2),
 (44,'role:assign'    ,'分配权限',2,43,'','',1),
 (48,'log:login'      ,'登录日志',1,40,'/system/login-log','',5),
 (49,'log:operation'  ,'操作日志',1,40,'/system/operation-log','',6);

-- 超级管理员拥有全部权限
INSERT IGNORE INTO `admin_role_permission` (`role_id`,`permission_id`)
SELECT 1, id FROM `admin_permission`;

-- 管理员(ADMIN)：预置驾驶舱 + 会员与订单菜单 + 商品查看/编辑权限，使其开箱即可管理商品。
-- （商品增删改的最终校验在后端按角色 SUPER_ADMIN/ADMIN 强制限定，与此处菜单权限相互独立。）
INSERT IGNORE INTO `admin_role_permission` (`role_id`,`permission_id`)
SELECT 6, id FROM `admin_permission`
 WHERE `permission_code` IN ('dashboard:view','order:menu','goods:list','goods:edit');

-- 清理：移除已下线的「功能配置」「内容运营/Banner/公告」菜单及其授权（对历史库幂等生效）。
DELETE FROM `admin_role_permission`
 WHERE `permission_id` IN (SELECT id FROM (SELECT id FROM `admin_permission`
   WHERE `permission_code` IN ('func:config','content:menu','banner:list','notice:list')) t);
DELETE FROM `admin_permission`
 WHERE `permission_code` IN ('func:config','content:menu','banner:list','notice:list');

-- 重命名：「会员与订单」一级菜单更名为「商城管理」（对历史库幂等生效，INSERT IGNORE 不更新已存在行）。
UPDATE `admin_permission` SET `permission_name`='商城管理'
 WHERE `permission_code`='order:menu' AND `permission_name`='会员与订单';

-- 清理：移除已下线的「权限菜单」「系统配置」菜单及其授权（对历史库幂等生效）。
DELETE FROM `admin_role_permission`
 WHERE `permission_id` IN (SELECT id FROM (SELECT id FROM `admin_permission`
   WHERE `permission_code` IN ('permission:tree','config:list','config:edit')) t);
DELETE FROM `admin_permission`
 WHERE `permission_code` IN ('permission:tree','config:list','config:edit');

-- 清理：移除已下线的「用户分析」「收入分析」「功能分析」菜单及其授权（对历史库幂等生效）。
DELETE FROM `admin_role_permission`
 WHERE `permission_id` IN (SELECT id FROM (SELECT id FROM `admin_permission`
   WHERE `permission_code` IN ('analytics:user','analytics:income','analytics:func')) t);
DELETE FROM `admin_permission`
 WHERE `permission_code` IN ('analytics:user','analytics:income','analytics:func');

-- 其余角色的权限可在「角色管理」中按需分配（此处不预置，避免与运营实际策略冲突）。
