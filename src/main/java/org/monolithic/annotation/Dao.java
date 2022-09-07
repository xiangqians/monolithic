package org.monolithic.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * DAO
 *
 * @author xiangqian
 * @date 22:52 2022/09/06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Dao {

    @AliasFor(annotation = Component.class)
    String value() default "";

}
