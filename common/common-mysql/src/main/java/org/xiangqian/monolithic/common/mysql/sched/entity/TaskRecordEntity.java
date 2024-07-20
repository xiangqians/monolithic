package org.xiangqian.monolithic.common.mysql.sched.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Data
@Schema(description = "任务调度记录表")
public class TaskRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "任务id")
    private Integer taskId;

    @Schema(description = "状态，0-执行中，1-成功，2-失败")
    private Byte status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "信息")
    private String msg;

}
