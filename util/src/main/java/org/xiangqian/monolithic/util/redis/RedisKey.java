package org.xiangqian.monolithic.util.redis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis是一种流行的开源内存数据库，支持多种数据类型
 *
 * @author xiangqian
 * @date 12:02 2024/06/01
 */
public class RedisKey {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisKey(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 判断是否存在某个前缀的键
     *
     * @param prefix
     * @return
     */
    public boolean hasKeyWithPrefix(String prefix) {
        return CollectionUtils.isNotEmpty(keyWithPrefix(prefix, 1));
    }

    /**
     * 根据键前缀获取键集合
     *
     * @param prefix
     * @param count
     * @return
     */
    public Set<String> keyWithPrefix(String prefix, int count) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            ScanOptions scanOptions = ScanOptions.scanOptions().match(prefix + "*").count(count).build();
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            Set<String> keys = null;
            while (cursor.hasNext()) {
                if (keys == null) {
                    keys = new HashSet<>(count);
                }
                String key = new String(cursor.next());
                keys.add(key);
            }
            return keys;
        });
    }

    /**
     * 判断指定的键是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return BooleanUtils.toBoolean(redisTemplate.hasKey(key));
    }

    /**
     * 设置缓存过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @return
     */
    public Boolean expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

}
