package org.xiangqian.monolithic.web;

import java.lang.annotation.*;

/**
 * 允许未经授权访问
 *
 * @author xiangqian
 * @date 20:47 2024/06/02
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Allow {
}
