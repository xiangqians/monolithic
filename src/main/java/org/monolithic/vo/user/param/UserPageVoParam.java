package org.monolithic.vo.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.monolithic.o.BoParam;
import org.monolithic.o.PoParam;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.param.UserPoParam;

/**
 * @author xiangqian
 * @date 22:33 2022/09/07
 */
@Data
@ApiModel(description = "用户分页信息")
public class UserPageVoParam extends PageRequest {

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户名")
    private String username;

    @Override
    public void post() {
        nickname = StringUtils.trimToNull(nickname);
        username = StringUtils.trimToNull(username);
        super.post();
    }

    @Override
    public <T extends PoParam> T convertToPoParam(Class<T> type) {

        if (type == UserPoParam.class) {
            UserPoParam poParam = new UserPoParam();
            poParam.setNickname(getNickname());
            poParam.setUsername(getUsername());
            return (T) poParam;
        }

        return super.convertToPoParam(type);
    }

}
