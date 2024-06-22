package org.xiangqian.monolithic.common.redis;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 13:22 2024/06/01
 */
public class RedisTest {

    protected Redis redis;

    @Test
    @SneakyThrows
    public void testPubSub() {
        String channel = "test-channel";
        redis.subscribe((message, pattern) -> {
            System.out.format("【订阅者1】%s, %s", pattern != null ? new String(pattern) : null, message).println();
        }, channel);

        redis.pSubscribe((message, pattern) -> {
            System.out.format("【订阅者2】%s, %s", pattern != null ? new String(pattern) : null, message).println();
        }, "test-*");

        while (true) {
            redis.publish(channel, String.format("Hello, %s", DateTimeUtil.format(LocalDateTime.now())));
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Before
    public final void before0() {
        redis = Redis.create();
        before();
    }

    protected void before() {
    }

}
