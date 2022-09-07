package org.monolithic.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.monolithic.constant.LockFlag;
import org.monolithic.vo.com.ComVo;

/**
 * @author xiangqian
 * @date 22:21 2022/09/07
 */
@Data
@ApiModel(description = "用户信息")
public class UserVo extends ComVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty(value = LockFlag.API_MODEL_PROPERTY_VALUE, allowableValues = LockFlag.API_MODEL_PROPERTY_ALLOWABLE_VALUES)
    private String lockFlag;

}
