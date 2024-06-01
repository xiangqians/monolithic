package org.xiangqian.monolithic.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.xiangqian.monolithic.biz.Code;

/**
 * 全局异常处理器
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception exception) {
        if (exception instanceof NoHandlerFoundException) {
            return response(Code.NOT_FOUND, exception.getMessage());
        }

        log.error("", exception);
        return response(Code.ERROR, "网络异常");
    }

    private ResponseEntity<Response> response(String code, String msg) {
        return new ResponseEntity<>(new Response(code, msg), HttpStatus.OK);
    }

}
