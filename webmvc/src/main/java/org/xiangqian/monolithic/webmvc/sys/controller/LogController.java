package org.xiangqian.monolithic.webmvc.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.common.biz.sys.service.LogService;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.LazyList;
import org.xiangqian.monolithic.common.mysql.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;
import org.xiangqian.monolithic.webmvc.WebmvcController;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@RestController
@Tag(name = "日志接口")
@RequestMapping("/api/sys/log")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class LogController extends WebmvcController {

    @Autowired
    private LogService logService;

    @GetMapping("/lazyList")
    @Operation(summary = "日志列表")
    public Result<LazyList<LogEntity>> lazyList(@ParameterObject LazyList<LogEntity> list, @ParameterObject LogEntity log) {
        return result(logService.lazyList(list, log));
    }

    @GetMapping("/page")
    @Operation(summary = "日志分页")
    public Result<Page<LogEntity>> page(@ParameterObject Page<LogEntity> page, @ParameterObject LogEntity log) {
        return result(logService.page(page, log));
    }

}
