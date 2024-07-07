package org.xiangqian.monolithic.webflux.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.common.biz.sys.service.LogService;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mysql.LazyList;
import org.xiangqian.monolithic.common.mysql.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;
import org.xiangqian.monolithic.webflux.WebfluxController;
import reactor.core.publisher.Mono;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@RestController
@Tag(name = "日志接口")
@RequestMapping("/api/sys/log")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class LogController extends WebfluxController {

    @Autowired
    private LogService logService;

    @GetMapping("/lazyList")
    @Operation(summary = "日志列表")
    public Mono<ResponseEntity<Result<LazyList<LogEntity>>>> lazyList(@ParameterObject LazyList<LogEntity> lazyList, @ParameterObject LogEntity logEntity) {
        return result(() -> logService.lazyList(lazyList, logEntity));
    }

    @GetMapping("/page")
    @Operation(summary = "日志分页")
    public Mono<ResponseEntity<Result<Page<LogEntity>>>> page(@ParameterObject Page<LogEntity> page, @ParameterObject LogEntity logEntity) {
        return result(() -> logService.page(page, logEntity));
    }

}
