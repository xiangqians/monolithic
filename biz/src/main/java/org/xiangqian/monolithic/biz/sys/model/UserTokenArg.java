package org.xiangqian.monolithic.biz.sys.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.xiangqian.monolithic.biz.sys.SysCode;

/**
 * @author xiangqian
 * @date 12:17 2024/06/08
 */
@Data
public abstract class UserTokenArg {

    @Schema(description = "密码")
    private String passwd;

    @Schema(description = "验证码")
    private String code;

    @NotNull(message = SysCode.USER_VERIFY_TYPE_NOT_EMPTY)
    @Schema(description = "验证类型：1-密码，2-验证码")
    private Byte type;

}
