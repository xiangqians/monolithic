package org.xiangqian.monolithic.web.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.auth.service.AuthService;
import org.xiangqian.monolithic.biz.auth.vo.AuthTokenRequest;
import org.xiangqian.monolithic.biz.auth.vo.AuthTokenResponse;
import org.xiangqian.monolithic.web.Response;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "授权接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    @Operation(summary = "获取令牌")
    public Response<AuthTokenResponse> token(@Valid @RequestBody AuthTokenRequest authRequest) {
        return new Response<>(Code.OK, authService.token(authRequest));
    }

    @RequestMapping("/revoke")
    @Operation(summary = "撤销令牌")
    public Response<Boolean> revoke() {
        return new Response<>(Code.OK, authService.revoke());
    }

}
