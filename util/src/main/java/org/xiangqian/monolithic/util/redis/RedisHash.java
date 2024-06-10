package org.xiangqian.monolithic.util.redis;

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
 * @date 12:16 2024/06/10
 */
public class RedisHash {

    private HashOperations<String, Object, Object> hashOperations;
    private String key;

    public RedisHash(HashOperations<String, Object, Object> hashOperations, String key) {
        this.hashOperations = hashOperations;
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

    // Redis数据类型之Sorted Set（有序集合）
    // 与Set类似，但每个元素都有一个分数(score)关联，可以按照分数排序，支持范围查询。
    // 使用场景：排行榜

    // Redis数据类型之Bitmap（位图）：使用位来表示某种状态，可以进行位操作，常用于统计、布隆过滤器等场景。
    // 使用场景：签到打卡、活跃用户等

    // Redis数据类型之HyperLogLog（基数统计）：用于估算集合中不重复元素的数量，占用固定空间，适用于大规模数据的基数统计。
    // 使用场景：在线用户数、统计访问量等

    // Redis数据类型之Geospatial（地理位置）：存储地理位置坐标，并支持距离计算和范围查找。
    // 使用场景：同城的人、同城的店等

}
