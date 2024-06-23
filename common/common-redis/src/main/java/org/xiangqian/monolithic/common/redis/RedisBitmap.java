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

    // 使用 Lua 脚本进行位图统计操作
    private static final RedisScript<Long> countScript = new DefaultRedisScript<>("return redis.call('bitcount', KEYS[1])", Long.class);

    // count_sum.lua
    // 计算多个位图中所有位的总和 Lua 脚本
    private static final RedisScript<Long> countSumScript = new DefaultRedisScript<>("""
            local sum = 0
            for i, key in ipairs(KEYS) do
                sum = sum + redis.call('BITCOUNT', key)
            end
            return sum
            """,
            Long.class);

    // count_sum.lua
    // 计算多个位图中所有位的总和 Lua 脚本
    private static final RedisScript<Long> unionCountSumScript = new DefaultRedisScript<>("""
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
     * @return 旧值
     */
    public Boolean set(long offset, boolean value) {
        Boolean oldValue = valueOperations.setBit(key, offset, value);
        return oldValue;
    }

    /**
     * 获取位操作
     * 获取指定 key 的位图中偏移量为 offset 的二进制位的值
     *
     * @param offset
     * @return
     */
    public Boolean get(long offset) {
        return valueOperations.getBit(key, offset);
    }

    /**
     * 查找位图中第一个设置为 1（或者 0）的位的位置
     *
     * @param bit
     * @return 如果没有，则返回 -1
     */
    public Long pos(boolean bit) {
        Long offset = redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitPos(key.getBytes(), bit));
        return offset;
    }

    /**
     * 统计位值为 1 的数量
     *
     * @return
     */
    public Long count() {
        return countV2();
    }

    /**
     * 统计位值为 1 的数量
     *
     * @param start 字节偏移量，不是比特偏移量
     * @param end   字节偏移量，不是比特偏移量
     * @return
     */
    public Long count(long start, long end) {
        return countV1(start, end);
    }

    private Long countV1() {
        return redisTemplate.execute(connection -> connection.bitCount(key.getBytes()), true);
    }

    private Long countV1(long start, long end) {
        return redisTemplate.execute(connection -> connection.bitCount(key.getBytes(), start, end), true);
    }

    private Long countV2() {
        return redisTemplate.execute(countScript, List.of(key));
    }

    /**
     * 计算多个位图中所有位的总和
     *
     * @param keys
     * @return
     */
    public Long countSum(String... keys) {
        return redisTemplate.execute(countSumScript, Arrays.asList(keys));
    }

}
