package org.xiangqian.monolithic.common.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.CodeException;
import org.xiangqian.monolithic.common.model.Result;

/**
 * Web异常处理器
 *
 * @author xiangqian
 * @date 19:39 2024/07/05
 */
@Slf4j
public abstract class WebExceptionHandler {

    protected Result<?> handle(Throwable throwable) {
        log.error("", throwable);

        if (throwable instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) throwable;
            HttpStatusCode httpStatusCode = errorResponse.getStatusCode();
            if (httpStatusCode == HttpStatus.NOT_FOUND) {
                return new Result<>(Code.NOT_FOUND);
            }

            if (httpStatusCode == HttpStatus.METHOD_NOT_ALLOWED) {
                return new Result<>(Code.METHOD_NOT_ALLOWED);
            }

            return new Result<>(Code.ERROR);
        }

        if (throwable instanceof CodeException) {
            return new Result<>(throwable.getMessage());
        }

        if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) throwable;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            return new Result<>(fieldError.getDefaultMessage());
        }

        return new Result(Code.ERROR);
    }

}
