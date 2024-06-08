package org.xiangqian.monolithic.biz.sys.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author xiangqian
 * @date 16:40 2024/06/01
 */
@Data
@Schema(description = "用户邮箱令牌参数信息")
public class UserTokenEmailArg extends UserTokenArg {

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "邮箱验证码")
    @Override
    public String getCode() {
        return super.getCode();
    }

}
