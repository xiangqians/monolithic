package org.xiangqian.monolithic.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis数据类型之String（字符串）
 * 存储文本、数字（整数或浮点数，并支持数值进行加法、减法等操作）、二进制数据，最大可以存储512MB的内容。
 * <p>
 * 使用场景：缓存、短信验证码、计数器、分布式session。
 *
 * @author xiangqian
 * @date 17:06 2024/06/22
 */
public class RedisString {

    private ValueOperations<String, Object> valueOperations;

    private RedisString(RedisTemplate<String, Object> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    /**
     * 设置键值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        valueOperations.set(key, value);
    }

    /**
     * 设置键值
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public void set(String key, Object value, Duration timeout) {
        valueOperations.set(key, value, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 设置键值子字符串
     *
     * @param key
     * @param offset
     * @param value
     */
    public void set(String key, long offset, String value) {
        valueOperations.set(key, value, offset);
    }

    /**
     * 设置键值，如果该键已存在，则不进行任何操作
     *
     * @param key   键
     * @param value 值
     */
    public Boolean setIfAbsent(String key, Object value) {
        return valueOperations.setIfAbsent(key, value);
    }

    /**
     * 设置键值，如果该键已存在，则不进行任何操作
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public Boolean setIfAbsent(String key, Object value, Duration timeout) {
        return valueOperations.setIfAbsent(key, value, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 批量设置多个键值
     *
     * @param kvMap
     */
    public void multiSet(Map<String, Object> kvMap) {
        valueOperations.multiSet(kvMap);
    }

    /**
     * 批量设置多个键值，如果键已存在，则不进行任何操作
     *
     * @param kvMap
     */
    public Boolean multiSetIfAbsent(Map<String, Object> kvMap) {
        return valueOperations.multiSetIfAbsent(kvMap);
    }

    /**
     * 追加值
     *
     * @param key
     * @param value
     * @return
     */
    public Integer append(String key, String value) {
        return valueOperations.append(key, value);
    }

    /**
     * 获取键值
     *
     * @param key 键
     * @return
     */
    public Object get(String key) {
        return valueOperations.get(key);
    }

    /**
     * 获取键值
     *
     * @param key   键
     * @param start
     * @param end
     * @return
     */
    public String get(String key, long start, long end) {
        return valueOperations.get(key, start, end);
    }

    /**
     * 获取键旧值并设置新值
     *
     * @param key      键
     * @param newValue 新值
     * @return 旧值
     */
    public Object getAndSet(String key, Object newValue) {
        Object oldValue = valueOperations.getAndSet(key, newValue);
        return oldValue;
    }

    /**
     * 批量获取多个键值
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(Collection<String> keys) {
        return valueOperations.multiGet(keys);
    }

    /**
     * 键值递增1
     *
     * @param key
     * @return
     */
    public Long increment(String key) {
        return valueOperations.increment(key);
    }

    /**
     * 键值递增delta
     *
     * @param key
     * @param delta
     * @return
     */
    public Long increment(String key, long delta) {
        return valueOperations.increment(key, delta);
    }

    /**
     * 键值递增delta
     *
     * @param key
     * @param delta
     * @return
     */
    public Double increment(String key, double delta) {
        return valueOperations.increment(key, delta);
    }

    /**
     * 键值递减1
     *
     * @param key
     * @return
     */
    public Long decrement(String key) {
        return valueOperations.decrement(key);
    }

    /**
     * 键值递减delta
     *
     * @param key
     * @param delta
     * @return
     */
    public Long decrement(String key, long delta) {
        return valueOperations.decrement(key, delta);
    }

    /**
     * 获取值长度
     *
     * @param key
     * @return
     */
    public Long size(String key) {
        return valueOperations.size(key);
    }

    private static RedisString redisString;

    protected static RedisString get(RedisTemplate<String, Object> redisTemplate) {
        if (redisString == null) {
            redisString = new RedisString(redisTemplate);
        }
        return redisString;
    }

}
