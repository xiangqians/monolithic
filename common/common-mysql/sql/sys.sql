/*
TINYINT
1 个字节
有符号范围：-128 到 127
无符号范围：0 到 255

SMALLINT
2 个字节
有符号范围：-32768 到 32767
无符号范围：0 到 65535

MEDIUMINT
3 个字节
有符号范围：-8388608 到 8388607
无符号范围：0 到 16777215

INT 或 INTEGER
4 个字节
有符号范围：-2147483648 到 2147483647
无符号范围：0 到 4294967295

BIGINT
8 个字节
有符号范围：-9223372036854775808 到 9223372036854775807
无符号范围：0 到 18446744073709551615

TEXT
最大存储容量为 65535 个字节或字符（约 64 KB）

MEDIUMTEXT
最大存储容量为 16777215 个字节或字符（约 16 MB）

LONGTEXT
最大存储容量为 4294967295 个字节或字符（约 4 GB）

*/


-- ------------------------------
-- Table structure for sys_tenant
-- ------------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`            INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id`     INT(8) UNSIGNED DEFAULT 0 COMMENT '租户id',
    `role_id`       INT(8) UNSIGNED DEFAULT 0 COMMENT '角色id',
    `name`          VARCHAR(64)       DEFAULT '' COMMENT '用户名',
    `email`         VARCHAR(64)       DEFAULT '' COMMENT '邮箱',
    `phone`         VARCHAR(64)       DEFAULT '' COMMENT '手机号',
    `passwd`        VARCHAR(128)      DEFAULT '' COMMENT '密码',
    `locked`        TINYINT           DEFAULT 0 COMMENT '是否已锁定，0-否，1-是',
    `deny_count`    TINYINT           DEFAULT 0 COMMENT '用户连续错误登陆次数，超过3次则锁定用户',
    `login_history` VARCHAR(512)      DEFAULT '' COMMENT '登录历史', -- [{"ip": "localhost", "time": "2024/05/30 21:55:18"}]
    `del`           TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time`      DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 密码：admin
INSERT INTO `sys_user`(`name`, `email`, `passwd`)
VALUES ('系统管理员', 'admin@xiangqain.org', '$2a$10$ZsS2bA7B7AQtIBBpW7xz3OIw3FWU0CnXX7HZMi6vBNt9ZNcA2RNGG');


-- ---------------------------------------
-- Table structure for sys_authority_group
-- ---------------------------------------
DROP TABLE IF EXISTS `sys_authority_group`;
CREATE TABLE `sys_authority_group`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `path`     VARCHAR(128)      DEFAULT '' COMMENT '路径',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限组表';


-- ---------------------------------
-- Table structure for sys_authority
-- ---------------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_id` INT(8) UNSIGNED DEFAULT 0 COMMENT '权限组id',
    `method`   VARCHAR(8)        DEFAULT '' COMMENT '方法',
    `path`     VARCHAR(128)      DEFAULT '' COMMENT '路径',
    `allow`    TINYINT           DEFAULT 0 COMMENT '是否允许未经授权访问，0-不允许，1-允许',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `code`     VARCHAR(64)       DEFAULT '' COMMENT '标识码',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- --------------------------------------
-- Table structure for sys_role_authority
-- --------------------------------------
DROP TABLE IF EXISTS `sys_role_authority`;
CREATE TABLE `sys_role_authority`
(
    `role_id`      INT(8) UNSIGNED NOT NULL COMMENT '角色id',
    `authority_id` INT(8) UNSIGNED NOT NULL COMMENT '权限id',
    `add_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `authority_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';


-- ---------------------------------
-- Table structure for sys_dict_type
-- ---------------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `code`     VARCHAR(64)       DEFAULT '' COMMENT '标识码',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';


-- ---------------------------------
-- Table structure for sys_dict_item
-- ---------------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_id`  INT(8) UNSIGNED NOT NULL COMMENT '字典类型id',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `value`    VARCHAR(64)       DEFAULT '' COMMENT '值',
    `sort`     SMALLINT          DEFAULT 0 COMMENT '排序',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';

