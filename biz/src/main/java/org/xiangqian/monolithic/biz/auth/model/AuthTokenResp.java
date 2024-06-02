package org.xiangqian.monolithic.biz.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 22:24 2024/06/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "授权令牌信息")
public class AuthTokenResp {

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "过期时间")
    private LocalDateTime exp;

}
