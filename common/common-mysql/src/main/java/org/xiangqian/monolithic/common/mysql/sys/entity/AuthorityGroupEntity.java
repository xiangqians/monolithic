package org.xiangqian.monolithic.common.mysql.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiangqian
 * @date 20:38 2024/06/12
 */
@Data
@Schema(description = "权限组信息")
public class AuthorityGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "备注")
    private String rem;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

    @Schema(description = "权限集合")
    private List<AuthorityEntity> authorities;

}
