package org.monolithic.vo.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.monolithic.o.Ppo;
import org.monolithic.o.Vpo;
import org.monolithic.po.param.UserPoParam;

import javax.validation.constraints.NotBlank;

/**
 * @author xiangqian
 * @date 22:20 2022/09/07
 */
@Data
@ApiModel(description = "新增用户信息")
public class UserAddVoParam implements Vpo {

    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty(value = "昵称", required = true)
    private String nickname;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Override
    public void post() {
        nickname = StringUtils.trimToNull(nickname);
        username = StringUtils.trimToNull(username);
        password = StringUtils.trimToNull(password);
    }

    @Override
    public <T extends Ppo> T convertToPoParam(Class<T> type) {

        if (type == UserPoParam.class) {
            UserPoParam poParam = new UserPoParam();
            poParam.setNickname(getNickname());
            poParam.setUsername(getUsername());
            poParam.setPassword(getPassword());
            return (T) poParam;
        }

        return Vpo.super.convertToPoParam(type);
    }

}
