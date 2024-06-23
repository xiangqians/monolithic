package org.xiangqian.monolithic.common.redis;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Map;

/**
 * @author xiangqian
 * @date 17:06 2024/06/22
 */
public class RedisStringTest {

    private RedisString redisString;

    @Before
    public void before() {
        Redis redis = Redis.create();
        redisString = redis.String();
    }

    @Test
    public void testSet() {
        redisString.set("test-string-1", 1);
        redisString.set("test-string-2", 2, Duration.ofSeconds(30));
    }

    @Test
    public void testSetIfAbsent() {
        Boolean result = redisString.setIfAbsent("test-string-1", 1);
        System.out.println(result);
        result = redisString.setIfAbsent("test-string-2", 2, Duration.ofSeconds(30));
        System.out.println(result);
    }

    @Test
    public void testMultiSet() {
        redisString.multiSet(Map.of("test-string-1", 1, "test-string-2", 2));

        Boolean result = redisString.multiSetIfAbsent(Map.of("test-string-1", 1, "test-string-2", 2));
        System.out.println(result);
    }

    @Test
    public void testGet() {
        Object value = redisString.get("test-string-1");
        System.out.println(value);
    }

    @Test
    public void testGetAndSet() {
        Object newValue = 100;
        Object oldValue = redisString.getAndSet("test-string-1", newValue);
        System.out.println(oldValue);
    }

    @Test
    public void testAppend() {
        String key = "test-string";
        redisString.set(key, 0, Duration.ofSeconds(30));
        Object value = redisString.get(key);
        System.out.println(value); // 0

        Integer result = redisString.append(key, "0");
        System.out.println(result); // 2
        value = redisString.get(key);
        System.out.println(value); // 00
    }


}
