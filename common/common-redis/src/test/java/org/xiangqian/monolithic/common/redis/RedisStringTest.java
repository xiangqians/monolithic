package org.xiangqian.monolithic.common.redis;

import org.junit.Test;

/**
 * @author xiangqian
 * @date 17:06 2024/06/22
 */
public class RedisStringTest extends RedisTest {

    private RedisString redisString;

    @Test
    public void test() {
        String key = "test";

//        redisString.set(key, 123);

        redisString.increment(key);

        Object value = redisString.get(key);
        System.out.println(value);
    }

    @Override
    protected void before() {
        redisString = redis.String();
    }

}
