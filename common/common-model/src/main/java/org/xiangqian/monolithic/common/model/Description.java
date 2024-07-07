package org.xiangqian.monolithic.common.model;

import java.lang.annotation.*;

/**
 * @author xiangqian
 * @date 15:08 2024/06/01
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    String value();

}
