package org.xiangqian.monolithic.common.redis;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 13:22 2024/06/01
 */
public class RedisTest {

    protected Redis redis;

    @Before
    public void before() {
        redis = Redis.create();
    }

    @Test
    public void testHasKey() {
        Boolean result = redis.hasKey("test-key");
        System.out.println(result);
    }

    @Test
    public void testGetKeysWithPrefix() {
        Set<String> keys = redis.getKeysWithPrefix("test-key", 30);
        System.out.println(keys);
    }

    @Test
    public void testHasKeyWithPrefix() {
        Boolean result = redis.hasKeyWithPrefix("test-key");
        System.out.println(result);
    }

    @Test
    public void testExpire() {
        Boolean result = redis.expire("test-key", Duration.ofSeconds(30));
        System.out.println(result);
    }

    @Test
    public void testDelete() {
        Boolean result = redis.delete("test-key");
        System.out.println(result);
    }

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

        int count = 30;
        while (count-- > 0) {
            redis.publish(channel, String.format("Hello, %s", DateTimeUtil.format(LocalDateTime.now())));
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
