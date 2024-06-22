package org.xiangqian.monolithic.web.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.LazyList;
import org.xiangqian.monolithic.biz.Page;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;
import org.xiangqian.monolithic.biz.sys.service.LogService;
import org.xiangqian.monolithic.biz.Response;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@RestController
@Tag(name = "日志接口")
@RequestMapping("/api/sys/log")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/list")
    @Operation(summary = "日志列表")
    public Response<LazyList<LogEntity>> list(@ParameterObject LazyList<LogEntity> list, @ParameterObject LogEntity log) {
        return new Response<>(Code.OK, logService.list(list, log));
    }

    @GetMapping("/page")
    @Operation(summary = "日志分页")
    public Response<Page<LogEntity>> page(@ParameterObject Page<LogEntity> page, @ParameterObject LogEntity log) {
        return new Response<>(Code.OK, logService.page(page, log));
    }

}
