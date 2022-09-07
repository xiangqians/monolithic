package org.monolithic.annotation;

import java.lang.annotation.*;

/**
 * @author xiangqian
 * @date 22:48 2022/09/07
 */
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocketGroups {

    DocketGroup[] value();

}
