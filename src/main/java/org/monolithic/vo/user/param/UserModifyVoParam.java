package org.monolithic.vo.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.monolithic.o.Ppo;
import org.monolithic.po.param.UserPoParam;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author xiangqian
 * @date 22:30 2022/09/07
 */
@Data
@ApiModel(description = "修改用户信息")
public class UserModifyVoParam extends UserAddVoParam {

    @Min(value = 1, message = "用户d必须大于0")
    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private Long id;

    @Override
    public <T extends Ppo> T convertToPoParam(Class<T> type) {

        if (type == UserPoParam.class) {
            UserPoParam poParam = super.convertToPoParam(UserPoParam.class);
            poParam.setId(getId());
            return (T) poParam;
        }

        return super.convertToPoParam(type);
    }

}
