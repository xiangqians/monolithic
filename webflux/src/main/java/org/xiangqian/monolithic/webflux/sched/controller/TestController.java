package org.xiangqian.monolithic.webflux.sched.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
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
    public Mono<ResponseEntity<Result<Boolean>>> c(ServerWebExchange exchange, @PathVariable Long id) {
        return result(exchange, () -> null);
    }

    @Allow
    @GetMapping("/c/{id}")
    @Operation(summary = "cid")
    public Mono<ResponseEntity<Result<Long>>> cid(ServerWebExchange exchange, @PathVariable Long id) {
        return result(exchange, () -> 1 / id);
    }

    @PostMapping("/token")
    @Operation(summary = "获取令牌1")
    public Mono<ResponseEntity<Result<UserTokenResult>>> token(ServerWebExchange exchange, @Valid @RequestBody UserTokenEmailArg authTokenReq) {
        return result(exchange, () -> null);
    }

    @Allow
    @GetMapping("/a")
    @Operation(summary = "a")
    public Mono<ResponseEntity<Result<Boolean>>> a(ServerWebExchange exchange) {
        return result(exchange, () -> null);
    }

    @Allow
    @GetMapping("/revoke")
    @Operation(summary = "撤销令牌")
    public Mono<ResponseEntity<Result<Boolean>>> revoke(ServerWebExchange exchange) {
        return result(exchange, () -> null);
    }

    @GetMapping("/e")
    @Operation(summary = "e")
    public Mono<ResponseEntity<Result<Boolean>>> e(ServerWebExchange exchange) {
        return result(exchange, () -> null);
    }

    @GetMapping("/b")
    @Operation(summary = "b")
    public Mono<ResponseEntity<Result<Boolean>>> b(ServerWebExchange exchange) {
        return result(exchange, () -> null);
    }

}

