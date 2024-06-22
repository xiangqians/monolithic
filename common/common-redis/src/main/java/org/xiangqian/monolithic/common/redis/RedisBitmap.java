package org.xiangqian.monolithic.common.redis;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;

/**
 * Redis数据类型之Bitmap（位图）：使用位来表示某种状态，可以进行位操作，常用于统计、布隆过滤器等场景。
 * <p>
 * Bitmap是一种位数组，可以对单个位进行设置、清除和查询操作。每一位(bit)可以存储0或1，通常用于表示布尔值（true/false）。
 * 常见使用场景
 * 1、用户签到记录
 * 2、存在性检查（如记录某项是否被访问过）
 * 3、标志位存储（如权限标志）
 * <p>
 * 单个bitmaps的最大长度是512MB，即2^32个比特位
 * bitmaps的最大优势是节省存储空间。比如在一个以自增id代表不同用户的系统中，我们只需要512MB空间就可以记录40亿用户的某个单一信息，相比mysql节省了大量的空间
 * 有两种类型的位操作：一类是对特定bit位的操作，比如设置/获取某个特定比特位的值。另一类是批量bit位操作，例如在给定范围内统计为1的比特位个数
 * <p>
 * 使用场景：签到打卡、活跃用户等
 *
 * @author xiangqian
 * @date 17:20 2024/06/22
 */
public class RedisBitmap {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private String key;

    // bit_count_sum.lua
    // 计算多个位图中所有位的总和 Lua 脚本
    private static final DefaultRedisScript<Long> bitCountSumScript = new DefaultRedisScript<>("""
            local sum = 0
            for i, key in ipairs(KEYS) do
                sum = sum + redis.call('BITCOUNT', key)
            end
            return sum
            """,
            Long.class);

    // bit_count_sum.lua
    // 计算多个位图中所有位的总和 Lua 脚本
    private static final DefaultRedisScript<Long> bitUnionCountSumScript = new DefaultRedisScript<>("""
            local sum = 0
            for i, key in ipairs(KEYS) do
                sum = sum + redis.call('BITCOUNT', key)
            end
            return sum
            """,
            Long.class);

    RedisBitmap(RedisTemplate<String, Object> redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.key = key;
    }

    /**
     * 设置位操作
     * 设置指定 key 的位图中偏移量为 offset 的二进制位的值
     *
     * @param offset
     * @param value
     * @return
     */
    public Boolean setBit(long offset, boolean value) {
        return valueOperations.setBit(key, offset, value);
    }

    /**
     * 获取位操作
     * 获取指定 key 的位图中偏移量为 offset 的二进制位的值
     *
     * @param offset
     * @return
     */
    public Boolean getBit(long offset) {
        return valueOperations.getBit(key, offset);
    }

    /**
     * 查找位图中第一个设置为 1（或者 0）的位的位置
     *
     * @param bit
     * @return
     */
    public Long bitPos(boolean bit) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitPos(key.getBytes(), bit));
    }

    /**
     * 统计位值为 1 的数量
     *
     * @return
     */
    public Long bitCount() {
        return bitCountV2();
    }

    private Long bitCountV1() {
        return redisTemplate.execute(connection -> connection.bitCount(key.getBytes()), true);
    }

    private Long bitCountV2() {
        // 使用 Lua 脚本进行位图统计操作
        String script = "return redis.call('bitcount', KEYS[1])";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        return redisTemplate.execute(redisScript, List.of(key));
    }

    /**
     * 计算多个位图中所有位的总和
     *
     * @param keys
     * @return
     */
    public Long bitCountSum(String... keys) {
        return redisTemplate.execute(bitCountSumScript, Arrays.asList(keys));
    }

    //BITCOUNT key [start end]：统计位为1的数量
    //BITOP operation destkey key [key ...]：位运算

}
