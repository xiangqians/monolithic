package org.xiangqian.monolithic.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import org.xiangqian.monolithic.common.biz.sys.service.SecurityService;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * @author xiangqian
 * @date 20:32 2023/10/13
 */
@Slf4j
public abstract class WebfluxController {

    @Autowired
    private SecurityService securityService;

    protected <T> Mono<ResponseEntity<Result<T>>> result(ServerWebExchange exchange, Callable<? extends T> supplier) {
        return Mono.fromCallable(() -> {
            UserEntity user = exchange.getAttribute(WebfluxSecurityFilter.PRINCIPAL_ATTRIBUTE);
            securityService.setUser(user);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Result<>(Code.OK, supplier.call()));
        });
    }

    protected Mono<ResponseEntity<Resource>> resource(ServerWebExchange exchange, Callable<Resource> supplier) {
        return Mono.fromCallable(() -> {
            UserEntity user = exchange.getAttribute(WebfluxSecurityFilter.PRINCIPAL_ATTRIBUTE);
            securityService.setUser(user);
            Resource resource = supplier.call();
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8))
                    .body(resource);
        });
    }

}
