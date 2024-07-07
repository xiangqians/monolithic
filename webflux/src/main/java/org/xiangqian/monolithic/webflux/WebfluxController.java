package org.xiangqian.monolithic.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
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

    protected <T> Mono<ResponseEntity<Result<T>>> result(Callable<? extends T> supplier) {
        return Mono.fromCallable(() -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Result<>(Code.OK, supplier.call())));
    }

    protected Mono<ResponseEntity<Resource>> resource(Callable<Resource> supplier) {
        return Mono.fromCallable(() -> {
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
