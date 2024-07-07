package org.xiangqian.monolithic.webmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.web.WebExceptionHandler;

/**
 * WebMvc异常处理器
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@RestControllerAdvice // @RestControllerAdvice 是 Spring Framework 提供的一个注解，用于全局处理 RESTful 服务中的异常
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebmvcExceptionHandler extends WebExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handle1(Exception exception) {
        return new ResponseEntity<>(handle(exception), HttpStatus.OK);
    }

}
