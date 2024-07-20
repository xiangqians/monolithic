package org.xiangqian.monolithic.common.mysql.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 21:22 2024/07/03
 */
@Data
@Schema(description = "角色表")
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标识码")
    private String code;

    @Schema(description = "备注")
    private String rem;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

}
