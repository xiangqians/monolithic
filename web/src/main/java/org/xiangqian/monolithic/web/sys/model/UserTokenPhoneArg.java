package org.xiangqian.monolithic.web.sys.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author xiangqian
 * @date 12:17 2024/06/08
 */
@Data
@Schema(description = "用户手机号令牌参数信息")
public class UserTokenPhoneArg extends UserTokenArg {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "短信验证码")
    @Override
    public String getCode() {
        return super.getCode();
    }

}
