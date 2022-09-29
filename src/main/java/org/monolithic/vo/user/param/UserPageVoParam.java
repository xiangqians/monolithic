package org.monolithic.vo.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.monolithic.o.Ppo;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.param.UserPoParam;
import org.monolithic.validation.MyAnno;

/**
 * @author xiangqian
 * @date 22:33 2022/09/07
 */
@Data
@ApiModel(description = "用户分页信息")
public class UserPageVoParam extends PageRequest {

    @MyAnno(message = "用户昵称校验失败")
    @ApiModelProperty("用户昵称")
    private String nickname;

    @MyAnno(message = "用户名校验失败")
    @ApiModelProperty("用户名")
    private String username;

    @Override
    public void post() {
//        nickname = StringUtils.trimToNull(nickname);
//        username = StringUtils.trimToNull(username);
        super.post();
    }

    @Override
    public <T extends Ppo> T convertToPoParam(Class<T> type) {

        if (type == UserPoParam.class) {
            UserPoParam poParam = new UserPoParam();
            poParam.setNickname(getNickname());
            poParam.setUsername(getUsername());
            return (T) poParam;
        }

        return super.convertToPoParam(type);
    }

}
