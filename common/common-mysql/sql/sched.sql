-- ------------------------------
-- Table structure for sched_task
-- ------------------------------
DROP TABLE IF EXISTS `sched_task`;
CREATE TABLE `sched_task`
(
    `id`       INT(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`     VARCHAR(64)       DEFAULT '' COMMENT '名称',
    `class`    VARCHAR(128)      DEFAULT '' COMMENT 'Spring Bean Class',
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
    `status`     TINYINT  DEFAULT 0 COMMENT '状态，0-执行中，1-成功，2-失败',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time`   DATETIME DEFAULT NULL COMMENT '结束时间',
    `msg`        TEXT     DEFAULT NULL COMMENT '信息',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度记录表';


/*

SELECT * FROM sched_task;

SELECT * FROM sched_task_record;

SELECT
t.`name`, t.class, t.cron, t.rem,
(CASE tr.`status` WHEN 0 THEN '执行中' WHEN 1 THEN '成功' WHEN 2 THEN '失败' ELSE '未知' END) AS 'status',
tr.start_time, tr.end_time, tr.msg
FROM sched_task t
JOIN sched_task_record tr ON tr.task_id = t.id
ORDER BY tr.id DESC
LIMIT 100
;

*/