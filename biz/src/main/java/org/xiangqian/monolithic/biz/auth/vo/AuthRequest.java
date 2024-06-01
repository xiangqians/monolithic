package org.xiangqian.monolithic.biz.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.xiangqian.monolithic.biz.auth.AuthCode;

/**
 * @author xiangqian
 * @date 16:40 2024/06/01
 */
@Data
@Schema(description = "授权请求")
public class AuthRequest {

    @Schema(description = "用户名或手机号")
    private String nop;

    @Schema(description = "密码或短信验证码")
    private String poc;

    @NotNull(message = AuthCode.TYPE_NOT_EMPTY)
    @Schema(description = "授权类型：1-用户名/密码，2-手机号/密码，3-手机号/短信验证码")
    private Byte type;

}
