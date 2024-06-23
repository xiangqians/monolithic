package org.xiangqian.monolithic.common.redis;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 在Redis中，Bitmap 和 Bitfield 是用于操作二进制数据的两种重要数据结构，它们提供了高效的存储和操作方式，适用于不同的使用场景。
 * Bitfield 是Redis 3.2引入的功能，允许你将字符串作为多个字段（field）的集合来处理，每个字段可以有不同的长度。
 * Bitfield 支持原子性的读取和修改操作，使得其比单纯的Bitmap更加灵活和强大。
 * <p>
 * 常见使用场景
 * 1、多状态标志存储（如权限管理）
 * 2、数字型数据的紧凑存储（如计数器）
 * 3、自定义小数据结构存储
 * <p>
 * 总结：
 * Bitmap：适用于简单的单个位操作和检查，用于布尔值的存储和查询。
 * Bitfields：适用于更复杂的位级操作，允许定义和操作自定义长度的字段，非常适合需要紧凑存储多种状态或计数器等应用场景。
 *
 * @author xiangqian
 * @date 17:21 2024/06/22
 */
public class RedisBitField {

    private RedisTemplate<String, Object> redisTemplate;
    private String key;

    RedisBitField(RedisTemplate<String, Object> redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    /**
     * 设置字段
     *
     * @param bitFieldType 位字段类型，{@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_64}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_64}
     * @param offset
     * @param value
     * @return
     */
    public Long set(BitFieldSubCommands.BitFieldType bitFieldType, long offset, int value) {
        List<Long> oldValues = redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().set(bitFieldType).valueAt(offset).to(value)), true);
        if (CollectionUtils.isNotEmpty(oldValues)) {
            return oldValues.get(0);
        }
        return null;
    }

    /**
     * 获取字段
     *
     * @param bitFieldType 位字段类型，{@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_64}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_64}
     * @param offset
     * @return
     */
    public Long get(BitFieldSubCommands.BitFieldType bitFieldType, long offset) {
        List<Long> values = redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().get(bitFieldType).valueAt(offset)), true);
        if (CollectionUtils.isNotEmpty(values)) {
            return values.get(0);
        }
        return null;
    }

    /**
     * 字段递增
     *
     * @param bitFieldType 位字段类型，{@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#INT_64}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_8}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_16}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_32}
     *                     {@link org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType#UINT_64}
     * @param offset
     * @param delta
     * @return
     */
    public Long increment(BitFieldSubCommands.BitFieldType bitFieldType, long offset, int delta) {
        List<Long> oldValues = redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().incr(bitFieldType).valueAt(offset).by(delta)), true);
        if (CollectionUtils.isNotEmpty(oldValues)) {
            return oldValues.get(0);
        }
        return null;
    }

}
