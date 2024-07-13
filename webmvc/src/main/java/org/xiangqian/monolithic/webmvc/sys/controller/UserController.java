package org.xiangqian.monolithic.webmvc.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.web.Allow;
import org.xiangqian.monolithic.webmvc.WebmvcController;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "用户接口")
@RequestMapping("/api/sys/user")
public class UserController extends WebmvcController {

    // @ParameterObject
    // The usage of @ParameterObject is wrong. This annotation extracts fields from parameter object.
    // You should simply use @Parameter swagger standard annotation instead, or mark the parameter explicitly as @RequestParam.
    //
    // @PostMapping(value = "/persons")
    // public void create(@Parameter(in = ParameterIn.QUERY) Long id, @RequestBody Object o){}
    // I have added control to prevent this error on bad usage of @ParameterObject annotation.
    //
    // Additionally, with the next release, it will be detected out of the box, without any extra swagger-annotations.
    //
    // @ParameterObject is designed for HTTP GET Methods.
    // Please have a look at the documentation:
    // https://springdoc.org/faq.html#how-can-i-map-pageable-spring-date-commons-object-to-correct-url-parameter-in-swagger-ui

    @Autowired
    private UserService service;

    @Allow
    @PostMapping("/token/email")
    @Operation(summary = "根据邮箱获取令牌")
    public Result<UserTokenResult> getTokenByEmail(@Valid @RequestBody UserTokenEmailArg arg) {
        return result(service.getTokenByEmail(arg));
    }

    @Allow
    @PostMapping("/token/phone")
    @Operation(summary = "根据手机号获取令牌")
    public Result<UserTokenResult> getTokenByEmail(@Valid @RequestBody UserTokenPhoneArg arg) {
        return result(service.getTokenByPhone(arg));
    }

    @DeleteMapping("/token/revoke")
    @Operation(summary = "撤销令牌")
    @SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
    public Result<Boolean> revokeToken() {
        return result(service.revokeToken());
    }

}
