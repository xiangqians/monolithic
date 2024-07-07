package org.xiangqian.monolithic.common.mysql.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author xiangqian
 * @date 11:34 2024/06/10
 */
@Data
@TableName("sys_role_authority")
@Schema(description = "角色权限信息")
public class RoleAuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "权限id")
    private Long authorityId;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

}
