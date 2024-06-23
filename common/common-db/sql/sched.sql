-- ------------------------------
-- Table structure for sched_task
-- ------------------------------
DROP TABLE IF EXISTS `sched_task`;
CREATE TABLE `sched_task`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `bean`     VARCHAR(64)       DEFAULT '' COMMENT 'Spring Bean名称',
    `cron`     VARCHAR(64)       DEFAULT '' COMMENT 'Spring CRON',
    `rem`      VARCHAR(128)      DEFAULT '' COMMENT '备注',
    `del`      TINYINT           DEFAULT 0 COMMENT '是否已删除，0-未删除，1-已删除',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `upd_time` DATETIME          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度表';


-- -------------------------------------
-- Table structure for sched_task_record
-- -------------------------------------
DROP TABLE IF EXISTS `sched_task_record`;
CREATE TABLE `sched_task_record`
(
    `id`         INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id`    INT(8) UNSIGNED DEFAULT 0 COMMENT '任务id',
    `start_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time`   DATETIME          DEFAULT NULL COMMENT '结束时间',
    `status`     TINYINT           DEFAULT 0 COMMENT '状态，0-成功，1-失败，2-取消',
    `msg`        TEXT              DEFAULT NULL COMMENT '信息',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度记录表';
