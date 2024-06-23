package org.xiangqian.monolithic.common.redis;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.BitFieldSubCommands;

import java.util.List;

/**
 * @author xiangqian
 * @date 14:05 2024/06/23
 */
public class RedisBitFieldTest {

    private Redis redis;

    @Before
    public void before() {
        redis = Redis.create();
    }

    @Test
    public void testSet() {
        RedisBitField redisBitField = redis.BitField("test-bitField");
        Long oldValue = redisBitField.set(BitFieldSubCommands.BitFieldType.INT_8, 100, 100);
        System.out.println(oldValue);
    }

    @Test
    public void testGet() {
        RedisBitField redisBitField = redis.BitField("test-bitField");
        Long value = redisBitField.get(BitFieldSubCommands.BitFieldType.INT_8, 100);
        System.out.println(value);

        value = redisBitField.get(BitFieldSubCommands.BitFieldType.INT_8, 101);
        System.out.println(value);
    }

    @Test
    public void testIncrement() {
        RedisBitField redisBitField = redis.BitField("test-bitField");
        Long oldValue = redisBitField.increment(BitFieldSubCommands.BitFieldType.INT_8, 100, 1);
        System.out.println(oldValue);
    }

}
