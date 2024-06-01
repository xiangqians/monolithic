package org.xiangqian.monolithic.biz;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis是一种流行的开源内存数据库，支持多种数据类型
 *
 * @author xiangqian
 * @date 12:02 2024/06/01
 */
public class Redis {

    private RedisTemplate<java.lang.String, Object> redisTemplate;
    private String string;

    public Redis(RedisTemplate<java.lang.String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.string = new String();
    }

    /**
     * 判断是否存在某个前缀的键
     *
     * @param prefix
     * @return
     */
    public boolean hasKeyWithPrefix(java.lang.String prefix) {
        return CollectionUtils.isNotEmpty(keyWithPrefix(prefix, 1));
    }

    /**
     * 根据键前缀获取键集合
     *
     * @param prefix
     * @param count
     * @return
     */
    public java.util.Set<java.lang.String> keyWithPrefix(java.lang.String prefix, int count) {
        return redisTemplate.execute((RedisCallback<java.util.Set<java.lang.String>>) connection -> {
            ScanOptions scanOptions = ScanOptions.scanOptions().match(prefix + "*").count(count).build();
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            java.util.Set<java.lang.String> keys = null;
            while (cursor.hasNext()) {
                if (keys == null) {
                    keys = new HashSet<>(count);
                }
                java.lang.String key = new java.lang.String(cursor.next());
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
    public boolean hasKey(java.lang.String key) {
        return BooleanUtils.toBoolean(redisTemplate.hasKey(key));
    }

    /**
     * 设置缓存过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @return
     */
    public Boolean expire(java.lang.String key, Duration timeout) {
        return redisTemplate.expire(key, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public Boolean delete(java.lang.String key) {
        return redisTemplate.delete(key);
    }

    public String string() {
        return string;
    }

    /**
     * Redis数据类型之String（字符串）
     * 存储文本、数字（整数或浮点数，并支持数值进行加法、减法等操作）、二进制数据，最大可以存储512MB的内容。
     * <p>
     * 使用场景：缓存、短信验证码、计数器、分布式session
     */
    public class String {

        private ValueOperations<java.lang.String, Object> valueOperations;

        public String() {
            this.valueOperations = redisTemplate.opsForValue();
        }

        /**
         * 设置键值
         *
         * @param key     键
         * @param value   值
         * @param timeout 过期时间
         */
        public void set(java.lang.String key, Object value, Duration timeout) {
            valueOperations.set(key, value, timeout.toSeconds(), TimeUnit.SECONDS);
        }

        public void set(java.lang.String key, Object value) {
            valueOperations.set(key, value);
        }

        /**
         * 设置键值，如果该键已存在，则不进行任何操作
         *
         * @param key     键
         * @param value   值
         * @param timeout 过期时间
         */
        public Boolean setIfAbsent(java.lang.String key, Object value, Duration timeout) {
            return valueOperations.setIfAbsent(key, value, timeout.toSeconds(), TimeUnit.SECONDS);
        }

        public Boolean setIfAbsent(java.lang.String key, Object value) {
            return valueOperations.setIfAbsent(key, value);
        }

        /**
         * 批量设置多个键值
         *
         * @param kvMap
         */
        public void multiSet(Map<java.lang.String, Object> kvMap) {
            valueOperations.multiSet(kvMap);
        }

        /**
         * 批量设置多个键值，如果键已存在，则不进行任何操作
         *
         * @param kvMap
         */
        public void multiSetIfAbsent(Map<java.lang.String, Object> kvMap) {
            valueOperations.multiSetIfAbsent(kvMap);
        }

        /**
         * 获取键值
         *
         * @param key 键
         * @return
         */
        public Object get(java.lang.String key) {
            return valueOperations.get(key);
        }

        /**
         * 获取键旧值并设置新值
         *
         * @param key      键
         * @param newValue 新值
         * @return oldValue（旧值）
         */
        public Object getAndSet(java.lang.String key, java.lang.String newValue) {
            return valueOperations.getAndSet(key, newValue);
        }

        /**
         * 批量获取多个键值
         *
         * @param keys
         * @return
         */
        public java.util.List<Object> multiGet(Collection<java.lang.String> keys) {
            return valueOperations.multiGet(keys);
        }

        /**
         * 键值递增1
         *
         * @param key
         * @return
         */
        public Long increment(java.lang.String key) {
            return valueOperations.increment(key);
        }

        /**
         * 键值递增delta
         *
         * @param key
         * @param delta
         * @return
         */
        public Long increment(java.lang.String key, long delta) {
            return valueOperations.increment(key, delta);
        }

        /**
         * 键值递增delta
         *
         * @param key
         * @param delta
         * @return
         */
        public Double increment(java.lang.String key, double delta) {
            return valueOperations.increment(key, delta);
        }

        /**
         * 键值递减1
         *
         * @param key
         * @return
         */
        public Long decrement(java.lang.String key) {
            return valueOperations.decrement(key);
        }

        /**
         * 键值递减delta
         *
         * @param key
         * @param delta
         * @return
         */
        public Long decrement(java.lang.String key, long delta) {
            return valueOperations.decrement(key, delta);
        }
    }

    public List list(java.lang.String key) {
        return new List(key);
    }

    /**
     * Redis数据类型之List（列表）
     * 有序的字符串列表，支持头部和尾部的插入、删除操作，可以实现队列、栈等数据结构。
     * <p>
     * 使用场景：发布订阅等
     */
    public class List {
        private ListOperations<java.lang.String, Object> listOperations;
        private java.lang.String key;

        public List(java.lang.String key) {
            this.listOperations = redisTemplate.opsForList();
            this.key = key;
        }

        /**
         * 在列表的左侧插入元素
         *
         * @param value 元素值
         * @return
         */
        public Long leftPush(Object value) {
            return listOperations.leftPush(key, value);
        }

        /**
         * 在指定元素前插入新元素
         *
         * @param newValue
         * @param existingValue
         * @return
         */
        public Long leftPush(Object newValue, Object existingValue) {
            return redisTemplate.opsForList().leftPush(key, newValue, existingValue);
        }

        /**
         * 在列表的右侧插入元素
         *
         * @param value 元素值
         * @return
         */
        public Long rightPush(Object value) {
            return listOperations.rightPush(key, value);
        }

        /**
         * 在指定元素后插入新元素
         *
         * @param newValue
         * @param existingValue
         * @return
         */
        public Long rightPush(Object newValue, Object existingValue) {
            return redisTemplate.opsForList().rightPush(key, newValue, existingValue);
        }

        /**
         * 获取列表指定索引位置的元素
         *
         * @param index 索引值，从0开始
         * @return
         */
        public Object index(long index) {
            return listOperations.index(key, index);
        }

        /**
         * 弹出列表的左侧元素
         *
         * @return
         */
        public Object leftPop() {
            return listOperations.leftPop(key);
        }

        /**
         * 弹出列表的右侧元素
         *
         * @return
         */
        public Object rightPop() {
            return listOperations.rightPop(key);
        }

        /**
         * 获取指定范围内的元素集合
         *
         * @param start
         * @param end
         * @return
         */
        public java.util.List<Object> range(long start, long end) {
            return listOperations.range(key, start, end);
        }

        /**
         * 移除列表中值为value的元素
         *
         * @param value
         * @return removedCount
         */
        public Object remove(Object value) {
            return redisTemplate.opsForList().remove(key, 0, value);
        }

        /**
         * 截取列表，保留指定范围内的元素
         *
         * @param start
         * @param end
         */
        public void trim(long start, long end) {
            redisTemplate.opsForList().trim(key, start, end);
        }

        /**
         * 获取列表大小
         *
         * @return
         */
        public Long size() {
            return listOperations.size(key);
        }
    }

    public Set set(java.lang.String key) {
        return new Set(key);
    }

    /**
     * Redis数据类型之Set（集合）
     * 无序的字符串集合，元素不重复，支持集合间的交集、并集、差集等操作。
     * <p>
     * 使用场景：共同好友、点赞或点踩等
     */
    public class Set {
        private SetOperations<java.lang.String, Object> setOperations;
        private java.lang.String key;

        public Set(java.lang.String key) {
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
        public java.util.Set<Object> members() {
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
        public java.util.Set<Object> intersect(java.lang.String otherKey) {
            return setOperations.intersect(key, otherKey);
        }

        /**
         * 计算集合的并集
         *
         * @param otherKey
         * @return
         */
        public java.util.Set<Object> union(java.lang.String otherKey) {
            return setOperations.union(key, otherKey);
        }

        /**
         * 计算集合的差集
         *
         * @param otherKey
         * @return
         */
        public java.util.Set<Object> difference(java.lang.String otherKey) {
            return setOperations.difference(key, otherKey);
        }
    }

    public Hash hash(java.lang.String key) {
        return new Hash(key);
    }

    /**
     * Redis数据类型之Hash（哈希）
     * 存储键值对的集合，适合存储对象的属性信息，支持对单个字段的读写操作。
     * <p>
     * 使用场景：存储对象
     */
    public class Hash {
        private HashOperations<java.lang.String, Object, Object> hashOperations;
        private java.lang.String key;

        public Hash(java.lang.String key) {
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
        public java.util.Set<Object> keys() {
            return hashOperations.keys(key);
        }

        /**
         * 获取hash中所有的值
         *
         * @return
         */
        public java.util.List<Object> values() {
            return hashOperations.values(key);
        }
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
