package org.xiangqian.monolithic.biz.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Data
@TableName("authority")
@Schema(description = "权限信息")
public class AuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "方法")
    private String method;

    @TableField("`path`")
    @Schema(description = "路径")
    private String path;

    @Schema(description = "允许未经授权访问，0-不允许，1-允许")
    private Byte allow;

    @Schema(description = "备注")
    private String rem;

    @TableLogic
    @Schema(description = "逻辑删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

}
