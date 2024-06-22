package org.xiangqian.monolithic.common.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis数据类型之Hash（哈希）
 * 存储键值对的集合，适合存储对象的属性信息，支持对单个字段的读写操作。
 * <p>
 * 使用场景：存储对象
 *
 * @author xiangqian
 * @date 17:17 2024/06/22
 */
public class RedisHash {

    private HashOperations<String, Object, Object> hashOperations;
    private String key;

    RedisHash(RedisTemplate<String, Object> redisTemplate, String key) {
        this.hashOperations = redisTemplate.opsForHash();
        this.key = key;
    }

    /**
     * 向hash中添加哈希键值
     *
     * @param hashKey 哈希键
     * @param value   值
     * @return
     */
    public void put(Object hashKey, Object value) {
        hashOperations.put(key, hashKey, value);
    }

    /**
     * 获取hash中指定哈希键值
     *
     * @param hashKey
     * @return
     */
    public Object get(Object hashKey) {
        return hashOperations.get(key, hashKey);
    }

    /**
     * 获取hash中所有的哈希键值
     *
     * @return
     */
    public Map<Object, Object> entries() {
        return hashOperations.entries(key);
    }

    /**
     * 检查hash中是否存在指定的哈希键
     *
     * @param hashKey
     * @return
     */
    public Boolean hasKey(Object hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    /**
     * 删除hash中一个或多个哈希键
     *
     * @param hashKeys
     * @return
     */
    public Long delete(Object... hashKeys) {
        return hashOperations.delete(key, hashKeys);
    }

    /**
     * 获取hash中所有的哈希键
     *
     * @return
     */
    public Set<Object> keys() {
        return hashOperations.keys(key);
    }

    /**
     * 获取hash中所有的值
     *
     * @return
     */
    public List<Object> values() {
        return hashOperations.values(key);
    }

}
