package org.xiangqian.monolithic.webflux.sched.controller;

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
import org.springframework.web.server.ServerWebExchange;
import org.xiangqian.monolithic.common.biz.sched.service.TaskRecordService;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;
import org.xiangqian.monolithic.common.web.Allow;
import org.xiangqian.monolithic.webflux.WebfluxController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author xiangqian
 * @date 23:33 2024/05/30
 */
@RestController
@Tag(name = "任务调度记录接口")
@RequestMapping("/api/sched/task/record")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TaskRecordController extends WebfluxController {

    @Autowired
    private TaskRecordService service;

    @Allow
    @GetMapping("/list")
    @Operation(summary = "日志列表")
    public Mono<ResponseEntity<Result<List<TaskRecordEntity>>>> list(ServerWebExchange exchange, @ParameterObject TaskRecordEntity entity) {
        return result(exchange, () -> service.list(entity));
    }

    @Allow
    @GetMapping("/lazyList")
    @Operation(summary = "日志延迟加载列表")
    public Mono<ResponseEntity<Result<LazyList<TaskRecordEntity>>>> lazyList(ServerWebExchange exchange, @ParameterObject LazyList<TaskRecordEntity> lazyList, @ParameterObject TaskRecordEntity entity) {
        return result(exchange, () -> service.lazyList(lazyList, entity));
    }

    @Allow
    @GetMapping("/page")
    @Operation(summary = "日志分页")
    public Mono<ResponseEntity<Result<Page<TaskRecordEntity>>>> page(ServerWebExchange exchange, @ParameterObject Page<TaskRecordEntity> page, @ParameterObject TaskRecordEntity entity) {
        return result(exchange, () -> service.page(page, entity));
    }

}

