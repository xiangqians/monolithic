package org.xiangqian.monolithic.websocket;

import java.lang.annotation.*;

/**
 * 消息接收者
 *
 * @author xiangqian
 * @date 20:00 2024/06/17
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Receiver {
    // 接收主题（topic）
    String value();
}
