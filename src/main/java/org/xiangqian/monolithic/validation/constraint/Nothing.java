package org.xiangqian.monolithic.validation.constraint;

import java.lang.annotation.*;

/**
 * 不对字段做任何校验
 *
 * @author xiangqian
 * @date 19:05 2023/05/11
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nothing {

    Class<?>[] groups();

}
