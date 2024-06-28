package org.xiangqian.monolithic.emqx;

import java.lang.annotation.*;

/**
 * @author xiangqian
 * @date 21:19 2024/06/28
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    String topicFilter();

    int qos();

}



