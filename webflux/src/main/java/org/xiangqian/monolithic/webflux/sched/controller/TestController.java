package org.xiangqian.monolithic.webflux.sched.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.web.Allow;
import org.xiangqian.monolithic.webflux.WebfluxController;
import reactor.core.publisher.Mono;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "测试接口123")
@RequestMapping("/api/test/1")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TestController extends WebfluxController {

    //    @GetMapping("/c")
//    @Operation(summary = "c")
    public Mono<ResponseEntity<Result<Boolean>>> c(@PathVariable Long id) {
        return result(() -> null);
    }

    @Allow
    @GetMapping("/c/{id}")
    @Operation(summary = "cid")
    public Mono<ResponseEntity<Result<Long>>> cid(@PathVariable Long id) {
        return result(() -> 1 / id);
    }

    @PostMapping("/token")
    @Operation(summary = "获取令牌1")
    public Mono<ResponseEntity<Result<UserTokenResult>>> token(@Valid @RequestBody UserTokenEmailArg authTokenReq) {
        return result(() -> null);
    }

    @Allow
    @GetMapping("/a")
    @Operation(summary = "a")
    public Mono<ResponseEntity<Result<Boolean>>> a() {
        return result(() -> null);
    }

    @Allow
    @GetMapping("/revoke")
    @Operation(summary = "撤销令牌")
    public Mono<ResponseEntity<Result<Boolean>>> revoke() {
        return result(() -> null);
    }

    @GetMapping("/e")
    @Operation(summary = "e")
    public Mono<ResponseEntity<Result<Boolean>>> e() {
        return result(() -> null);
    }

    @GetMapping("/b")
    @Operation(summary = "b")
    public Mono<ResponseEntity<Result<Boolean>>> b() {
        return result(() -> null);
    }

}

