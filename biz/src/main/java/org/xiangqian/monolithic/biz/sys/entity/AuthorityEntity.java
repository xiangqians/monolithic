package org.xiangqian.monolithic.biz.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Data
@TableName("authority")
@Schema(description = "权限信息")
public class AuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "角色id")
    @TableField(exist = false)
    private List<Long> roleIds;

    @Schema(description = "方法")
    private String method;

    @TableField("`path`")
    @Schema(description = "路径")
    private String path;

    @Schema(description = "是否允许未经授权访问，0-不允许，1-允许")
    private Byte allow;

    @Schema(description = "备注")
    private String rem;

    @TableLogic
    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

    public void setRoleIdsStr(String roleIdsStr) {
        if (StringUtils.isNotEmpty(roleIdsStr)) {
            this.roleIds = Arrays.stream(roleIdsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        }
    }

}
