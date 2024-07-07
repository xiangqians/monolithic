package org.xiangqian.monolithic.webflux.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.web.Allow;
import org.xiangqian.monolithic.webflux.WebfluxController;
import reactor.core.publisher.Mono;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "用户接口")
@RequestMapping("/api/sys/user")
public class UserController extends WebfluxController {

    @Autowired
    private UserService service;

    @Allow
    @PostMapping("/token/email")
    @Operation(summary = "根据邮箱获取令牌")
    public Mono<ResponseEntity<Result<UserTokenResult>>> getTokenByEmail(@Valid @RequestBody UserTokenEmailArg arg) {
        return result(() -> service.getTokenByEmail(arg));
    }

    @Allow
    @PostMapping("/token/phone")
    @Operation(summary = "根据手机号获取令牌")
    public Mono<ResponseEntity<Result<UserTokenResult>>> getTokenByEmail(@Valid @RequestBody UserTokenPhoneArg arg) {
        return result(() -> service.getTokenByPhone(arg));
    }

    @RequestMapping("/token/revoke")
    @Operation(summary = "撤销令牌")
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    public Mono<ResponseEntity<Result<Boolean>>> revokeToken() {
        return result(() -> service.revokeToken());
    }


}
