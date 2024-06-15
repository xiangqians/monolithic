/*
TINYINT
1个字节
有符号范围：-128到127
无符号范围：0到255

SMALLINT
2个字节
有符号范围：-32768到32767
无符号范围：0到65535

MEDIUMINT
3个字节
有符号范围：-8388608到8388607
无符号范围：0到16777215

INT或INTEGER
4个字节
有符号范围：-2147483648到2147483647
无符号范围：0到4294967295

BIGINT
8个字节
有符号范围：-9223372036854775808到9223372036854775807
无符号范围：0到18446744073709551615

TEXT
最大存储容量为 65,535 个字节或字符（约 64 KB）

MEDIUMTEXT
最大存储容量为 16,777,215 个字节或字符（约 16 MB）

LONGTEXT
最大存储容量为 4,294,967,295 个字节或字符（约 4 GB）

*/


-- --------------------------
-- Table structure for tenant
-- --------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `rem`      VARCHAR(64)       DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';


-- ------------------------
-- Table structure for user
-- ------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
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
INSERT INTO `user`(`name`, `email`, `passwd`)
VALUES ('系统管理员', 'admin@xiangqain.org', '$2a$10$ZsS2bA7B7AQtIBBpW7xz3OIw3FWU0CnXX7HZMi6vBNt9ZNcA2RNGG');


-- ------------------------
-- Table structure for role
-- ------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `code`     VARCHAR(16)       DEFAULT '' COMMENT '识别码',
    `rem`      VARCHAR(64)       DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- -----------------------------------
-- Table structure for authority_group
-- -----------------------------------
DROP TABLE IF EXISTS `authority_group`;
CREATE TABLE `authority_group`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pid`      INT(8) UNSIGNED DEFAULT 0 COMMENT '父组id',
    `path`     VARCHAR(128)      DEFAULT '' COMMENT '路径',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限组表';


-- -----------------------------
-- Table structure for authority
-- -----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`
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


-- ----------------------------------
-- Table structure for role_authority
-- ----------------------------------
DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority`
(
    `role_id`      INT(8) UNSIGNED NOT NULL COMMENT '角色id',
    `authority_id` INT(8) UNSIGNED NOT NULL COMMENT '权限id',
    `add_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `authority_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';


-- -----------------------
-- Table structure for log
-- -----------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`
(
    `id`           INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      INT(8) UNSIGNED DEFAULT 0 COMMENT '用户id',
    `authority_id` INT(8) UNSIGNED DEFAULT 0 COMMENT '权限id',
    `address`      VARCHAR(64)       DEFAULT '' COMMENT '远程地址',
    `method`       VARCHAR(8)        DEFAULT '' COMMENT '请求方法',
    `url`          VARCHAR(512)      DEFAULT '' COMMENT '请求地址',
    `body`         MEDIUMTEXT        DEFAULT NULL COMMENT '请求报文',
    `code`         VARCHAR(64)       DEFAULT '' COMMENT '状态码',
    `time`         MEDIUMINT UNSIGNED DEFAULT 0 COMMENT '耗时，单位ms',
    `add_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志表';
