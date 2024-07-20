package org.xiangqian.monolithic.common.mysql.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Data
@Schema(description = "权限信息")
public class AuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "权限组id")
    private Long groupId;

    @Schema(description = "方法")
    private String method;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "是否允许未经授权访问，0-不允许，1-允许")
    private Byte allow;

    @Schema(description = "备注")
    private String rem;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

    /**
     * 处理方法（@RequestMapping）
     */
    @Schema(hidden = true)
    private Method handleMethod;

    @Schema(description = "所属角色id集合")
    private Set<Long> roleIds;

    public void setRoleIdsStr(String roleIdsStr) {
        if (StringUtils.isNotEmpty(roleIdsStr)) {
            this.roleIds = Arrays.stream(roleIdsStr.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        }
    }

}
