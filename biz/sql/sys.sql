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


-- -----------------------------
-- Table structure for authority
-- -----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
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
