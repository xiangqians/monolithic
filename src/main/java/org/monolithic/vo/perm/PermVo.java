package org.monolithic.vo.perm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.monolithic.vo.com.ComVo;

/**
 * @author xiangqian
 * @date 23:16 2022/09/07
 */
@Data
@ApiModel(description = "权限信息")
public class PermVo extends ComVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("权限父id")
    private Long parentId;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("权限允许权限方法，GET、POST、PUT、DELETE")
    private String method;

    @ApiModelProperty("权限路径")
    private String path;

}
