package org.xiangqian.monolithic.web.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.web.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.web.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.web.sys.model.UserTokenResult;
import org.xiangqian.monolithic.web.sys.service.UserService;
import org.xiangqian.monolithic.web.Allow;
import org.xiangqian.monolithic.biz.Response;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "用户接口")
@RequestMapping("/api/sys/user")
public class UserController {

    @Autowired
    private UserService service;

    @Allow
    @PostMapping("/token/email")
    @Operation(summary = "根据邮箱获取令牌")
    public Response<UserTokenResult> getTokenByEmail(@Valid @RequestBody UserTokenEmailArg arg) {
        return new Response<>(Code.OK, service.getTokenByEmail(arg));
    }

    @Allow
    @PostMapping("/token/phone")
    @Operation(summary = "根据手机号获取令牌")
    public Response<UserTokenResult> getTokenByEmail(@Valid @RequestBody UserTokenPhoneArg arg) {
        return new Response<>(Code.OK, service.getTokenByPhone(arg));
    }

    @RequestMapping("/token/revoke")
    @Operation(summary = "撤销令牌")
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    public Response<Boolean> revokeToken() {
        return new Response<>(Code.OK, service.revokeToken());
    }

}
