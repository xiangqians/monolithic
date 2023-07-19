-- -----------------------
-- Table structure for tmp
-- -----------------------
DROP TABLE IF EXISTS `tmp`;
CREATE TABLE `tmp`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`  INT(8) UNSIGNED DEFAULT 0 COMMENT '提问者id',
    `cat`      VARCHAR(8)        DEFAULT '' COMMENT '类别，字典type：question_cat',
    `title`    VARCHAR(128)      DEFAULT '' COMMENT '标题',
    `content`  TEXT COMMENT '内容',
    `imgs`     JSON              DEFAULT NULL COMMENT '图片集',
    `views`    INT(8) UNSIGNED DEFAULT 0 COMMENT '浏览量',
    `del`      CHAR(1)           DEFAULT '0' COMMENT '删除标识，0-正常，1-删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tmp';



-- --------------------------------------------------------------------------------------------------------------------
-- https://github.com/spring-attic/spring-security-oauth/blob/main/spring-security-oauth2/src/test/resources/schema.sql
-- --------------------------------------------------------------------------------------------------------------------


-- ----------------------------------------
-- Table structure for oauth_client_details
-- ---------------------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`
(
    `client_id`                VARCHAR(64) NOT NULL COMMENT '客户端id',
    `resource_ids`             JSON                 DEFAULT NULL COMMENT '资源ids',
    `secret_required`          CHAR(1)              DEFAULT '1' COMMENT '密钥是否必须，0-不必须；1-必须',
    `client_secret`            VARCHAR(64)          DEFAULT '' COMMENT '客户端密码，client_secret字段不能直接是 secret 的原始值，需要经过加密',
    `scope`                    JSON                 DEFAULT NULL COMMENT '该客户端允许授权的范围，定义客户端的权限，这里只是一个标识，资源服务可以根据这个权限进行鉴权',
    `authorized_grant_types`   JSON                 DEFAULT NULL COMMENT '该客户端允许授权的类型',
    `registered_redirect_uris` JSON                 DEFAULT NULL COMMENT '跳转的uri',
    `authorities`              JSON                 DEFAULT NULL COMMENT 'authorities',
    `access_token_validity`    INT(8) DEFAULT 7200 COMMENT '访问令牌有效期，单位：s',
    `refresh_token_validity`   INT(8) DEFAULT 259200 COMMENT '刷新令牌有效期，单位：s',
    `auto_approve`             CHAR(1)              DEFAULT '1' COMMENT '0-跳转到授权页面；1-不跳转，直接发令牌',
    `desc`                     VARCHAR(128)         DEFAULT '' COMMENT '描述',
    `addl_info`                JSON                 DEFAULT NULL COMMENT '附加信息',
    `del_flag`                 CHAR(1)              DEFAULT '0' COMMENT '删除标识，0-正常，1-已删除',
    `create_time`              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`              DATETIME             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端详情';
-- client_secret
-- 原始密码：123456
-- 加密后：$2a$10$yzWoLiGj/gfiDDnkcUnNkeoXUGE5mHOe034p4xbIkHxQxS7D3iJKa
INSERT INTO `oauth_client_details` (`client_id`,`resource_ids`,`secret_required`,`client_secret`,`scope`,
`authorized_grant_types`,
`registered_redirect_uris`,
`authorities`,
`access_token_validity`,
`refresh_token_validity`,
`auto_approve`,
`addl_info`)
VALUES ('d595c7492eac11ed8488000c29e749d2',
'["monolithic"]','1','$2a$10$yzWoLiGj/gfiDDnkcUnNkeoXUGE5mHOe034p4xbIkHxQxS7D3iJKa',
'["all"]',
'["password", "implicit", "authorization_code", "refresh_token", "client_credentials"]',
'["https://www.google.com/"]',
'[]',
7200,
259200,
'1',
NULL);



-- --------------------------------------
-- Table structure for oauth_access_token
-- --------------------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`
(
    `authentication_id` VARCHAR(256) NOT NULL,
    `token_id`          VARCHAR(256)          DEFAULT NULL,
    `token`             BLOB                  DEFAULT NULL,
    `user_name`         VARCHAR(256)          DEFAULT NULL,
    `client_id`         VARCHAR(256)          DEFAULT NULL,
    `authentication`    BLOB                  DEFAULT NULL,
    `refresh_token`     VARCHAR(256)          DEFAULT NULL,
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`authentication_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权令牌数据表';


-- ---------------------------------------
-- Table structure for oauth_refresh_token
-- ---------------------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`
(
    `token_id`       VARCHAR(256) NOT NULL,
    `token`          BLOB                  DEFAULT NULL,
    `authentication` BLOB                  DEFAULT NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权刷新令牌数据表';


-- ------------------------------
-- Table structure for oauth_code
-- ------------------------------
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`
(
    `code`           VARCHAR(256) NOT NULL COMMENT '存储服务端系统生成的code的值（未加密）',
    `authentication` BLOB                  DEFAULT NULL COMMENT '存储将AuthorizationRequestHolder.java对象序列化后的二进制数据',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权码数据存储表';


-- ------------------------
-- Table structure for user
-- ------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `nickname`    VARCHAR(64)          DEFAULT '' COMMENT '昵称',
    `username`    VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    `password`    VARCHAR(128)         DEFAULT '' COMMENT '密码',
    `desc`        VARCHAR(128)         DEFAULT '' COMMENT '描述',
    `addl_info`   JSON                 DEFAULT NULL COMMENT '附加信息',
    `lock_flag`   CHAR(1)              DEFAULT '0' COMMENT '锁定标记，0-正常，1-锁定',
    `del_flag`    CHAR(1)              DEFAULT '0' COMMENT '删除标识，0-正常，1-已删除',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_user_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
-- INSERT
INSERT INTO `user`(`nickname`, `username`, `password`)
VALUES ('系统管理员', 'admin', '$2a$10$IVzj1Wd.ZQdOIWdb1htQjexU94uoNeuk1crlQ9ExVupPi0Iy1uv.C');
-- password: 123456


-- ------------------------
-- Table structure for role
-- ------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        VARCHAR(64)          DEFAULT '' COMMENT '角色名称',
    `code`        VARCHAR(32) NOT NULL UNIQUE COMMENT '角色码',
    `desc`        VARCHAR(128)         DEFAULT '' COMMENT '角色描述',
    `del_flag`    CHAR(1)              DEFAULT '0' COMMENT '删除标识，0-正常，1-已删除',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
-- INSERT
INSERT INTO `role`(`name`, `code`)
VALUES ('系统管理员', 'SYS_ADMIN');


-- -----------------------------
-- Table structure for user_role
-- -----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `user_id`     INT(8) UNSIGNED NOT NULL COMMENT '用户id',
    `role_id`     INT(8) UNSIGNED NOT NULL COMMENT '角色id',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';
-- INSERT
INSERT INTO `user_role`(`user_id`, role_id)
VALUES (1, 1);


-- ------------------------
-- Table structure for perm
-- ------------------------
DROP TABLE IF EXISTS `perm`;
CREATE TABLE `perm`
(
    `id`          INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id`   INT(8) UNSIGNED DEFAULT 0 COMMENT '权限父id',
    `name`        VARCHAR(64)           DEFAULT '' COMMENT '权限名称',
    `method`      VARCHAR(8)            DEFAULT '' COMMENT '权限允许权限方法，GET、POST、PUT、DELETE',
    `path`        VARCHAR(128) NOT NULL UNIQUE COMMENT '权限路径',
    `desc`        VARCHAR(128)          DEFAULT '' COMMENT '权限描述',
    `del_flag`    CHAR(1)               DEFAULT '0' COMMENT '删除标识，0-正常，1-已删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';


-- -----------------------------
-- Table structure for role_perm
-- -----------------------------
DROP TABLE IF EXISTS `role_perm`;
CREATE TABLE `role_perm`
(
    `role_id`     INT(8) UNSIGNED NOT NULL COMMENT '角色id',
    `perm_id`     INT(8) UNSIGNED NOT NULL COMMENT '权限id',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `perm_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';


-- ---------------------------
-- Table structure for sys_log
-- ---------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`             INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `req_ip`         VARCHAR(64)       DEFAULT '' COMMENT '请求ip',
    `req_port`       VARCHAR(64)       DEFAULT '' COMMENT '请求端口',
    `req_param`      TEXT              DEFAULT NULL COMMENT '请求参数',
    `reqd_time`      INT(8) DEFAULT 0 COMMENT '请求所需要的时间，ms',

    -- User-Agent
    `brows_name`     VARCHAR(64)       DEFAULT '' COMMENT '浏览器名称',
    `brows_type`     VARCHAR(64)       DEFAULT '' COMMENT '浏览器类型',
    `brows_group`    VARCHAR(64)       DEFAULT '' COMMENT '浏览器家族',
    `brows_mfr`      VARCHAR(64)       DEFAULT '' COMMENT '浏览器生产厂商',
    `brows_re`       VARCHAR(64)       DEFAULT '' COMMENT '浏览器使用的渲染引擎',
    `brows_ver`      VARCHAR(64)       DEFAULT '' COMMENT '浏览器版本',
    `os_name`        VARCHAR(64)       DEFAULT '' COMMENT '操作系统名',
    `os_device_type` VARCHAR(64)       DEFAULT '' COMMENT '访问设备类型',
    `os_group`       VARCHAR(64)       DEFAULT '' COMMENT '操作系统家族',
    `os_mfr`         VARCHAR(64)       DEFAULT '' COMMENT '操作系统生产厂商',

    `user_id`        INT(8) DEFAULT 0 COMMENT '操作用户id',
    `perm_id`        VARCHAR(64)       DEFAULT '' COMMENT '操作权限id',
    `exc_flag`       CHAR(1)           DEFAULT '0' COMMENT '异常标识，0-正常，1-异常',
    `exc_msg`        TEXT              DEFAULT NULL COMMENT '异常信息',

    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志信息表';
