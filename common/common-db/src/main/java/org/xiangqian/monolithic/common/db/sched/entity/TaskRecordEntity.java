package org.xiangqian.monolithic.common.db.sched.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Data
@TableName("sched_task_record")
@Schema(description = "任务调度记录表")
public class TaskRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "任务id")
    private Integer taskId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态，0-成功，1-失败，2-取消")
    @TableField("`status`")
    private Byte status;

    @Schema(description = "信息")
    private String msg;

}
