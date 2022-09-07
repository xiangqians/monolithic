package org.monolithic.annotation;

import java.lang.annotation.*;

/**
 * 公共资源标识
 *
 * @author xiangqian
 * @date 22:09 2022/09/07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PubRes {
}
