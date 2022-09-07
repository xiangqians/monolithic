package org.monolithic.annotation;

import java.lang.annotation.*;

/**
 * swagger document 组
 *
 * @author xiangqian
 * @date 22:48 2022/09/07
 */
@Inherited
@Documented
@Repeatable(DocketGroups.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocketGroup {

    String name();

    String[] tags() default {};

}
