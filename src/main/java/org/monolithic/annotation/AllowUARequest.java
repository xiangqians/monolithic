package org.monolithic.annotation;

import java.lang.annotation.*;

/**
 * 允许未经授权请求
 *
 * @author xiangqian
 * @date 21:01 2022/09/07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AllowUARequest {
}
