package org.xiangqian.monolithic.common.mysql.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 21:22 2024/07/03
 */
@Data
@TableName("sys_tenant")
@Schema(description = "租户表")
public class TenantEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "名称")
    @TableField("`name`")
    private String name;

    @Schema(description = "备注")
    private String rem;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    @TableLogic
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

}