package org.xiangqian.monolithic.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

/**
 * Redis数据类型之Set（集合）
 * 无序的字符串集合，元素不重复，支持集合间的交集、并集、差集等操作。
 * <p>
 * 使用场景：共同好友、点赞或点踩等
 *
 * @author xiangqian
 * @date 17:14 2024/06/22
 */
public class RedisSet {

    private SetOperations<String, Object> setOperations;
    private String key;

    RedisSet(RedisTemplate<String, Object> redisTemplate, String key) {
        this.setOperations = redisTemplate.opsForSet();
        this.key = key;
    }

    /**
     * 向集合中添加一个或多个元素
     *
     * @param values
     * @return
     */
    public Long add(Object... values) {
        return setOperations.add(key, values);
    }

    /**
     * 从集合中移除一个或多个元素
     *
     * @param values
     * @return
     */
    public Long remove(Object... values) {
        return setOperations.remove(key, values);
    }

    /**
     * 检查集合是否包含指定元素
     *
     * @param value
     * @return
     */
    public Boolean isMember(Object value) {
        return setOperations.isMember(key, value);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @return
     */
    public Object randomMember() {
        return setOperations.randomMember(key);
    }

    /**
     * 弹出集合中的一个随机元素
     *
     * @return
     */
    public Object pop() {
        return setOperations.pop(key);
    }

    /**
     * 获取集合中的所有元素
     *
     * @return
     */
    public Set<Object> members() {
        return setOperations.members(key);
    }

    /**
     * 获取集合的大小
     *
     * @return
     */
    public Long size() {
        return setOperations.size(key);
    }

    /**
     * 计算集合的交集
     *
     * @param otherKey
     * @return
     */
    public Set<Object> intersect(String otherKey) {
        return setOperations.intersect(key, otherKey);
    }

    /**
     * 计算集合的并集
     *
     * @param otherKey
     * @return
     */
    public Set<Object> union(String otherKey) {
        return setOperations.union(key, otherKey);
    }

    /**
     * 计算集合的差集
     *
     * @param otherKey
     * @return
     */
    public Set<Object> difference(String otherKey) {
        return setOperations.difference(key, otherKey);
    }

}
