package org.xiangqian.monolithic.common.redis;

import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * Redis数据类型之Sorted Set（有序集合）
 * 与Set类似，但每个元素都有一个分数(score)关联，可以按照分数排序，支持范围查询。
 * 使用场景：排行榜
 *
 * @author xiangqian
 * @date 17:16 2024/06/22
 */
public class RedisSortedSet {

    private ZSetOperations<String, Object> zSetOperations;
    private String key;

    RedisSortedSet(RedisTemplate<String, Object> redisTemplate, String key) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.key = key;
    }

    /**
     * 添加元素
     *
     * @param value 值
     * @param score 分数
     * @return
     */
    public Boolean add(Object value, double score) {
        return zSetOperations.add(key, value, score);
    }

    /**
     * 添加多个元素
     *
     * @param value1
     * @param score1
     * @param value2
     * @param score2
     * @return
     */
    public Long add(Object value1, double score1,
                    Object value2, double score2) {
        return zSetOperations.add(key, Set.of(new DefaultTypedTuple<>(value1, score1),
                new DefaultTypedTuple<>(value2, score2)));
    }

    /**
     * 获取有序集合中指定成员的分值
     *
     * @param value
     * @return
     */
    public Double score(Object value) {
        return zSetOperations.score(key, value);
    }

    /**
     * 增加有序集合中指定成员的分值
     *
     * @param value
     * @param delta
     * @return
     */
    public Double incrementScore(Object value, double delta) {
        return zSetOperations.incrementScore(key, value, delta);
    }

    /**
     * 获取有序集合中指定范围内的成员
     * 获取 Sorted Set key 中指定范围内的成员列表，按照分数从低到高排序
     *
     * @param start
     * @param end
     * @return
     */
    public Set<Object> range(long start, long end) {
        return zSetOperations.range(key, start, end);
    }

    /**
     * 获取 Sorted Set key 中指定范围内的成员列表，按照分数从高到低排序
     *
     * @param start
     * @param end
     * @return
     */
    public Set<Object> reverseRange(long start, long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    /**
     * 获取有序集合中指定范围的成员及其分值
     *
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(long start, long end) {
        return zSetOperations.rangeWithScores(key, start, end);
    }

    /**
     * 移除有序集合中的指定成员
     *
     * @param values
     * @return
     */
    public Long remove(Object... values) {
        return zSetOperations.remove(key, values);
    }

    /**
     * 获取有序集合的大小（成员数量）
     *
     * @return
     */
    public Long size() {
        return zSetOperations.size(key);
    }

}
