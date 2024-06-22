package org.xiangqian.monolithic.web.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.web.Allow;
import org.xiangqian.monolithic.biz.Response;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "测试接口123")
@RequestMapping("/api/test/1")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TestController {

    //    @GetMapping("/c")
//    @Operation(summary = "c")
    public Response<Boolean> c(@PathVariable Long id) {
        return new Response<>(Code.OK, null);
    }

    @Allow
    @GetMapping("/c/{id}")
    @Operation(summary = "cid")
    public Response<Long> cid(@PathVariable Long id) {
        return new Response<>(Code.OK, 1 / id);
    }

    @PostMapping("/token")
    @Operation(summary = "获取令牌1")
    public Response<UserTokenResult> token(@Valid @RequestBody UserTokenEmailArg authTokenReq) {
        return new Response<>(Code.OK, null);
    }

    @Allow
    @GetMapping("/a")
    @Operation(summary = "a")
    public Response<Boolean> a() {
        return new Response<>(Code.OK, null);
    }

    @Allow
    @GetMapping("/revoke")
    @Operation(summary = "撤销令牌")
    public Response<Boolean> revoke() {
        return new Response<>(Code.OK, null);
    }

    @GetMapping("/e")
    @Operation(summary = "e")
    public Response<Boolean> e() {
        return new Response<>(Code.OK, null);
    }

    @GetMapping("/b")
    @Operation(summary = "b")
    public Response<Boolean> b() {
        return new Response<>(Code.OK, null);
    }

}

