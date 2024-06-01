package org.xiangqian.monolithic.web.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/c")
    @Operation(summary = "c")
    public Response<Boolean> c() {
        return new Response<>(Code.OK, null);
    }

    @PostMapping("/token")
    @Operation(summary = "获取令牌")
    public Response<AuthTokenResponse> token(@Valid @RequestBody AuthTokenRequest authRequest) {
        return new Response<>(Code.OK, null);
    }

    @GetMapping("/a")
    @Operation(summary = "a")
    public Response<Boolean> a() {
        return new Response<>(Code.OK, null);
    }

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
