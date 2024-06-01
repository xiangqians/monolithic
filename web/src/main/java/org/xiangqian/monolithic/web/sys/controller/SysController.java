package org.xiangqian.monolithic.web.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.auth.vo.AuthTokenRequest;
import org.xiangqian.monolithic.biz.auth.vo.AuthTokenResponse;
import org.xiangqian.monolithic.web.Response;

/**
 * @author xiangqian
 * @date 00:07 2024/06/02
 */
@RestController
@RequestMapping("/api/sys")
@Tag(name = "系统接口")
public class SysController {

    @PostMapping("/token")
    @Operation(summary = "获取令牌")
    public Response<AuthTokenResponse> token(@Valid @RequestBody AuthTokenRequest authRequest) {
        return new Response<>(Code.OK, null);
    }

    @RequestMapping("/revoke")
    @Operation(summary = "撤销令牌")
    public Response<Boolean> revoke() {
        return new Response<>(Code.OK, null);
    }

}
