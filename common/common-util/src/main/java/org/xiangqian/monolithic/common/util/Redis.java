package org.xiangqian.monolithic.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Redis（Remote Dictionary Server）是一个开源的内存数据库，遵守 BSD 协议，它提供了一个高性能的键值（key-value）存储系统，常用于缓存、消息队列、会话存储等应用场景。
 * 1、性能极高：Redis 以其极高的性能而著称，能够支持每秒数十万次的读写操作。这使得Redis成为处理高并发请求的理想选择，尤其是在需要快速响应的场景中，如缓存、会话管理、排行榜等。
 * 2、丰富的数据类型：Redis 不仅支持基本的键值存储，还提供了丰富的数据类型，包括字符串、列表、集合、哈希表、有序集合等。这些数据类型为开发者提供了灵活的数据操作能力，使得Redis可以适应各种不同的应用场景。
 * 3、原子性操作：Redis 的所有操作都是原子性的，这意味着操作要么完全执行，要么完全不执行。这种特性对于确保数据的一致性和完整性至关重要，尤其是在高并发环境下处理事务时。
 * 4、持久化：Redis 支持数据的持久化，可以将内存中的数据保存到磁盘中，以便在系统重启后恢复数据。这为 Redis 提供了数据安全性，确保数据不会因为系统故障而丢失。
 * 5、支持发布/订阅模式：Redis 内置了发布/订阅模式（Pub/Sub），允许客户端之间通过消息传递进行通信。这使得 Redis 可以作为消息队列和实时数据传输的平台。
 * 6、单线程模型：尽管 Redis 是单线程的，但它通过高效的事件驱动模型来处理并发请求，确保了高性能和低延迟。单线程模型也简化了并发控制的复杂性。
 * 7、主从复制：Redis 支持主从复制，可以通过从节点来备份数据或分担读请求，提高数据的可用性和系统的伸缩性。
 * 8、应用场景广泛：Redis 被广泛应用于各种场景，包括但不限于缓存系统、会话存储、排行榜、实时分析、地理空间数据索引等。
 * 9、社区支持：Redis 拥有一个活跃的开发者社区，提供了大量的文档、教程和第三方库，这为开发者提供了强大的支持和丰富的资源。
 * 10、跨平台兼容性：Redis 可以在多种操作系统上运行，包括 Linux、macOS 和 Windows，这使得它能够在不同的技术栈中灵活部署。
 * <p>
 * Redis中文网
 * https://redis.com.cn
 * <p>
 * Redis数据类型：
 * https://redis.io/docs/latest/develop/data-types
 *
 * @author xiangqian
 * @date 12:02 2024/06/01
 */
public class Redis {

    private RedisTemplate<java.lang.String, Object> redisTemplate;
    private RedissonClient redissonClient;

    public Redis(RedisTemplate<java.lang.String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * 是否存在某个前缀的键
     *
     * @param prefix
     * @return
     */
    public boolean hasKeyWithPrefix(java.lang.String prefix) {
        return CollectionUtils.isNotEmpty(getKeysWithPrefix(prefix, 1));
    }

    /**
     * 根据键前缀获取键集合
     *
     * @param prefix
     * @param count
     * @return
     */
    public java.util.Set<java.lang.String> getKeysWithPrefix(java.lang.String prefix, int count) {
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
     * 指定的键是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(java.lang.String key) {
        return BooleanUtils.toBoolean(redisTemplate.hasKey(key));
    }

    /**
     * 设置缓存键过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @return
     */
    public Boolean expire(java.lang.String key, Duration timeout) {
        return redisTemplate.expire(key, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 删除缓存键
     *
     * @param key
     */
    public Boolean delete(java.lang.String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除缓存键集合
     *
     * @param keys
     * @return
     */
    public Long delete(java.util.Set<java.lang.String> keys) {
        return redisTemplate.delete(keys);
    }

    private String string;

    public String String() {
        if (string == null) {
            string = new String(redisTemplate);
        }
        return string;
    }

    /**
     * Redis数据类型之String（字符串）
     * 存储文本、数字（整数或浮点数，并支持数值进行加法、减法等操作）、二进制数据，最大可以存储512MB的内容。
     * <p>
     * 使用场景：缓存、短信验证码、计数器、分布式session。
     */
    public static class String {
        private ValueOperations<java.lang.String, Object> valueOperations;

        private String(RedisTemplate<java.lang.String, Object> redisTemplate) {
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
         * @return 旧值
         */
        public Object getAndSet(java.lang.String key, java.lang.String newValue) {
            Object oldValue = valueOperations.getAndSet(key, newValue);
            return oldValue;
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

    public List List(java.lang.String key) {
        return new List(redisTemplate, key);
    }

    /**
     * Redis数据类型之List（列表）
     * 有序的字符串列表，支持头部和尾部的插入、删除操作，可以实现队列、栈等数据结构。
     * <p>
     * 使用场景：发布订阅等
     */
    public static class List {
        private ListOperations<java.lang.String, Object> listOperations;
        private java.lang.String key;

        private List(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
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
            return listOperations.leftPush(key, newValue, existingValue);
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
            return listOperations.rightPush(key, newValue, existingValue);
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
            return listOperations.remove(key, 0, value);
        }

        /**
         * 截取列表，保留指定范围内的元素
         *
         * @param start
         * @param end
         */
        public void trim(long start, long end) {
            listOperations.trim(key, start, end);
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

    public Set Set(java.lang.String key) {
        return new Set(redisTemplate, key);
    }

    /**
     * Redis数据类型之Set（集合）
     * 无序的字符串集合，元素不重复，支持集合间的交集、并集、差集等操作。
     * <p>
     * 使用场景：共同好友、点赞或点踩等
     */
    public static class Set {
        private SetOperations<java.lang.String, Object> setOperations;
        private java.lang.String key;

        private Set(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
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

    public SortedSet SortedSet(java.lang.String key) {
        return new SortedSet(redisTemplate, key);
    }

    /**
     * Redis数据类型之Sorted Set（有序集合）
     * 与Set类似，但每个元素都有一个分数(score)关联，可以按照分数排序，支持范围查询。
     * 使用场景：排行榜
     */
    public static class SortedSet {
        private ZSetOperations<java.lang.String, Object> zSetOperations;
        private java.lang.String key;

        private SortedSet(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
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
            return zSetOperations.add(key, java.util.Set.of(new DefaultTypedTuple<>(value1, score1),
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
        public java.util.Set<Object> range(long start, long end) {
            return zSetOperations.range(key, start, end);
        }

        /**
         * 获取 Sorted Set key 中指定范围内的成员列表，按照分数从高到低排序
         *
         * @param start
         * @param end
         * @return
         */
        public java.util.Set<Object> reverseRange(long start, long end) {
            return zSetOperations.reverseRange(key, start, end);
        }

        /**
         * 获取有序集合中指定范围的成员及其分值
         *
         * @param start
         * @param end
         * @return
         */
        public java.util.Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(long start, long end) {
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

    public Hash Hash(java.lang.String key) {
        return new Hash(redisTemplate, key);
    }

    /**
     * Redis数据类型之Hash（哈希）
     * 存储键值对的集合，适合存储对象的属性信息，支持对单个字段的读写操作。
     * <p>
     * 使用场景：存储对象
     */
    public static class Hash {
        private HashOperations<java.lang.String, Object, Object> hashOperations;
        private java.lang.String key;

        private Hash(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
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

    public Bitmap Bitmap(java.lang.String key) {
        return new Bitmap(redisTemplate, key);
    }

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
     */
    public static class Bitmap {
        private RedisTemplate<java.lang.String, Object> redisTemplate;
        private ValueOperations<java.lang.String, Object> valueOperations;
        private java.lang.String key;

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

        private Bitmap(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
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
            java.lang.String script = "return redis.call('bitcount', KEYS[1])";
            RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            return redisTemplate.execute(redisScript, java.util.List.of(key));
        }

        /**
         * 计算多个位图中所有位的总和
         *
         * @param keys
         * @return
         */
        public Long bitCountSum(java.lang.String... keys) {
            return redisTemplate.execute(bitCountSumScript, Arrays.asList(keys));
        }

        //BITCOUNT key [start end]：统计位为1的数量
        //BITOP operation destkey key [key ...]：位运算

    }

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
     */
    public static class BitField {
        private RedisTemplate<java.lang.String, Object> redisTemplate;
        private java.lang.String key;

        private BitField(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
            this.redisTemplate = redisTemplate;
            this.key = key;
        }

        /**
         * 设置字段
         *
         * @param offset
         * @param value
         * @return
         */
        public java.util.List<Long> set(long offset, int value) {
            return redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().set(BitFieldSubCommands.BitFieldType.unsigned(3)).valueAt(offset).to(value)), true);
        }

        // 获取字段
        public java.util.List<Long> get(long offset) {
            return redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(3)).valueAt(offset)), true);
        }

        /**
         * 字段递增
         *
         * @param offset
         * @param increment
         * @return
         */
        public java.util.List<Long> increment(long offset, int increment) {
            return redisTemplate.execute((connection) -> connection.bitField(key.getBytes(), BitFieldSubCommands.create().incr(BitFieldSubCommands.BitFieldType.unsigned(3)).valueAt(offset).by(increment)), true);
        }

    }

    public Geospatial Geospatial(java.lang.String key) {
        return new Geospatial(redisTemplate, key);
    }

    /**
     * Redis数据类型之Geospatial（地理位置）：存储地理位置坐标，并支持距离计算和范围查找。
     * <p>
     * 使用场景：同城的人、同城的店等
     * 应用场景：如位置服务、地理位置推荐等
     */
    public static class Geospatial {
        private GeoOperations<java.lang.String, Object> geoOperations;
        private java.lang.String key;

        private Geospatial(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
            this.geoOperations = redisTemplate.opsForGeo();
            this.key = key;
        }

        /**
         * 添加地理位置数据
         *
         * @param x
         * @param y
         * @param member
         */
        public void add(double x, double y, Object member) {
            geoOperations.add(key, new Point(x, y), member);
        }

        /**
         * 获取地理位置数据的经纬度
         *
         * @param members
         * @return
         */
        public java.util.List<Point> position(Object... members) {
            return geoOperations.position(key, members);
        }

        /**
         * 计算两个地理位置之间的距离
         *
         * @param member1
         * @param member2
         * @param metric  度量单位，用于在地理空间查询中指定距离的度量方式
         *                {@link org.springframework.data.redis.domain.geo.Metrics#METERS} 米（Meter），这是国际单位制 (SI) 中的基本长度单位。
         *                {@link org.springframework.data.redis.domain.geo.Metrics#KILOMETERS} 千米（Kilometers）。1千米等于1000米。
         *                {@link org.springframework.data.redis.domain.geo.Metrics#MILES} 英里（Miles），主要在美国、英国等使用的非公制单位。1英里约等于1.60934千米。
         *                {@link org.springframework.data.redis.domain.geo.Metrics#FEET} 英尺（Feet），在非公制国家常用，1英尺等于12英寸或0.3048米.
         * @return
         */
        public Distance distance(Object member1, Object member2, Metric metric) {
            return geoOperations.distance(key, member1, member2, metric);
        }

        /**
         * 根据给定的中心点和半径范围进行搜索
         *
         * @param centerX
         * @param centerY
         * @param radius
         * @param metric
         * @return
         */
        public GeoResults<RedisGeoCommands.GeoLocation<Object>> radius(double centerX, double centerY, double radius, Metric metric) {
            return geoOperations.radius(key, new Circle(new Point(centerX, centerY), new Distance(radius, metric)),
                    // 通过args参数，可以设置诸如排序、限制返回结果数量、包括距离信息等附加信息。
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                            // 包含距离信息
                            .includeDistance()
                            // 距离升序
                            .sortAscending());
        }

        /**
         * 根据给定的成员作为中心点和半径范围进行搜索
         *
         * @param member
         * @param radius
         * @param metric
         * @return
         */
        public GeoResults<RedisGeoCommands.GeoLocation<Object>> radius(Object member, double radius, Metric metric) {
            return geoOperations.radius(key, member, new Distance(radius, metric),
                    // 通过args参数，可以设置诸如排序、限制返回结果数量、包括距离信息等附加信息。
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                            // 包含距离信息
                            .includeDistance()
                            // 距离升序
                            .sortAscending());
        }
    }

    public Stream Stream(java.lang.String key) {
        return new Stream(redisTemplate, key);
    }

    /**
     * Redis Streams 是 Redis 5.0 引入的数据结构，适用于处理消息队列和日志等应用场景。
     */
    public static class Stream {
        private RedisTemplate<java.lang.String, Object> redisTemplate;
        private StreamOperations<java.lang.String, java.lang.String, java.lang.String> streamOperations;
        private java.lang.String key;

        private Stream(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
            this.redisTemplate = redisTemplate;
            this.streamOperations = redisTemplate.opsForStream();
            this.key = key;
        }

        /**
         * 创建一个消费者组
         * 如果已经存在会抛出异常
         *
         * @param groupName  消费者组名称
         * @param readOffset 读取偏移量，在消费者组模式下，有不同的偏移量选项：
         *                   {@link org.springframework.data.redis.connection.stream.ReadOffset#latest()} 只读取新的消息
         *                   {@link org.springframework.data.redis.connection.stream.ReadOffset#lastConsumed} 读取所有未确认的消息（包括历史消息）
         * @return
         */
        public java.lang.String createGroup(java.lang.String groupName, ReadOffset readOffset) {
            return streamOperations.createGroup(key, readOffset, groupName);
        }

        /**
         * 获取所有消费者组
         *
         * @return
         */
        public StreamInfo.XInfoGroups getGroups() {
            return streamOperations.groups(key);
        }

        /**
         * 获取所有消费者组中的所有消费者
         *
         * @param groupName 消费者组名称
         * @return
         */
        public StreamInfo.XInfoConsumers getConsumers(java.lang.String groupName) {
            return streamOperations.consumers(key, groupName);
        }

        /**
         * 获取消费者组中挂起的消息
         *
         * @param groupName 消费者组名称
         * @return
         */
        public PendingMessagesSummary pending(java.lang.String groupName) {
            return streamOperations.pending(key, groupName);
        }

        /**
         * 获取指定消费者在消费者组中挂起的消息
         *
         * @param groupName    消费者组名称
         * @param consumerName 消费者名称
         * @return
         */
        public PendingMessages pending(java.lang.String groupName, java.lang.String consumerName) {
            return streamOperations.pending(key, Consumer.from(groupName, consumerName));
        }

        /**
         * 获取消费者组中指定范围内的挂起消息
         *
         * @param groupName 消费者组名称
         * @param range     消息范围
         * @param count     消息数量限制
         * @return
         */
        public PendingMessages pending(java.lang.String groupName, Range<?> range, long count) {
            return streamOperations.pending(key, groupName, range, count);
        }

        /**
         * 获取指定消费者在消费者组中指定范围内的挂起消息
         *
         * @param groupName    消费者组名称
         * @param consumerName 消费者名称
         * @param range        消息范围
         * @param count        消息数量限制
         * @return
         */
        public PendingMessages pending(java.lang.String groupName, java.lang.String consumerName, Range<?> range, long count) {
            return streamOperations.pending(key, Consumer.from(groupName, consumerName), range, count);
        }

        /**
         * 删除消费者组中的消费者
         *
         * @param groupName    消费者组名称
         * @param consumerName 消费者名称
         * @return
         */
        public Boolean deleteConsumer(java.lang.String groupName, java.lang.String consumerName) {
            return streamOperations.deleteConsumer(key, Consumer.from(groupName, consumerName));
        }

        /**
         * 销毁一个消费者组
         *
         * @param groupName 消费者组名称
         * @return
         */
        public Boolean destroyGroup(java.lang.String groupName) {
            return streamOperations.destroyGroup(key, groupName);
        }

        /**
         * 向流中添加一条记录
         *
         * @param map 要添加的值
         * @return
         */
        public RecordId add(Map<java.lang.String, java.lang.String> map) {
            return streamOperations.add(MapRecord.create(key, map));
        }

        //read(StreamReadOptions readOptions, StreamOffset<K>... streams)
        //从一个或多个流中读取记录。
        //StreamReadOptions readOptions: 读取选项配置（如块时间、消息计数等）。
        //StreamOffset<K>... streams: 要读取的流和偏移量。

        public java.util.List<MapRecord<java.lang.String, java.lang.String, java.lang.String>> read(java.lang.String groupName, java.lang.String consumerName) {
            return streamOperations.read(Consumer.from(groupName, consumerName),
                    StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                    StreamOffset.create(key, ReadOffset.lastConsumed()));
        }

        //read(Class<HK> targetType, StreamReadOptions readOptions, StreamOffset<K>... streams)
        //
        //从一个或多个流中读取记录，并将结果映射到指定类型。
        //Class<HK> targetType: 目标类型。
        //StreamReadOptions readOptions: 读取选项配置。
        //StreamOffset<K>... streams: 要读取的流和偏移量。
        //read(Consumer consumer, StreamReadOptions readOptions, StreamOffset<K>... streams)
        //
        //从一个或多个流中读取记录，使用消费者组模式。
        //Consumer consumer: 消费者组信息。
        //StreamReadOptions readOptions: 读取选项配置。
        //StreamOffset<K>... streams: 要读取的流和偏移量。
        //range(K key, Range<String> range)
        //
        //获取指定范围内的流记录。
        //K key: 流的键。
        //Range<String> range: 范围（起始 ID 和结束 ID）。
        //range(K key, Range<String> range, Limit limit)
        //
        //获取指定范围内的流记录，带有记录数量限制。
        //K key: 流的键。
        //Range<String> range: 范围。
        //Limit limit: 记录数量限制。
        //reverseRange(K key, Range<String> range)
        //
        //逆序获取指定范围内的流记录。
        //K key: 流的键。
        //Range<String> range: 范围。
        //reverseRange(K key, Range<String> range, Limit limit)
        //
        //逆序获取指定范围内的流记录，带有记录数量限制。
        //K key: 流的键。
        //Range<String> range: 范围。
        //Limit limit: 记录数量限制。

        //删除记录
        //delete(K key, String... recordIds)
        //删除流中的一条或多条记录。
        //K key: 流的键。
        //String... recordIds: 要删除的记录 ID 列表。


        //截断流
        //trim(K key, long count)
        //
        //修剪流到指定长度。
        //K key: 流的键。
        //long count: 流长度限制。
        //trim(K key, long count, boolean approximateTrimming)
        //
        //修剪流到指定长度，可选择近似修剪。
        //K key: 流的键。
        //long count: 流长度限制。
        //boolean approximateTrimming: 是否进行近似修剪。


        //其他操作
        //size(K key)
        //
        //获取流的长度。
        //K key: 流的键。
        //info(K key)
        //
        //获取流的基本信息。
        //K key: 流的键。


        //// 随机范围读取
        //<V> List<ObjectRecord<K, V>> range(Class<V> targetType, K key, Range<String> range)
        //<V> List<ObjectRecord<K, V>> range(Class<V> targetType, K key, Range<String> range, Limit limit)
        //
        //// 根据消息ID或者偏移量读取
        //List<MapRecord<K, HK, HV>> read(StreamOffset<K>... streams)
        //<V> List<ObjectRecord<K, V>> read(Class<V> targetType, StreamOffset<K>... streams)
        //List<MapRecord<K, HK, HV>> read(StreamReadOptions readOptions, StreamOffset<K>... streams)
        //<V> List<ObjectRecord<K, V>> read(Class<V> targetType, StreamReadOptions readOptions, StreamOffset<K>... streams)
        //List<MapRecord<K, HK, HV>> read(Consumer consumer, StreamOffset<K>... streams)
        //<V> List<ObjectRecord<K, V>> read(Class<V> targetType, Consumer consumer, StreamOffset<K>... streams)
        //List<MapRecord<K, HK, HV>> read(Consumer consumer, StreamReadOptions readOptions, StreamOffset<K>... streams)
        //List<ObjectRecord<K, V>> read(Class<V> targetType, Consumer consumer, StreamReadOptions readOptions, StreamOffset<K>... streams)
        //
        //// 随机逆向范围读取
        //List<MapRecord<K, HK, HV>> reverseRange(K key, Range<String> range)
        //List<MapRecord<K, HK, HV>> reverseRange(K key, Range<String> range, Limit limit)
        //<V> List<ObjectRecord<K, V>> reverseRange(Class<V> targetType, K key, Range<String> range)
        //<V> List<ObjectRecord<K, V>> reverseRange(Class<V> targetType, K key, Range<String> range, Limit limit)
        //
        //// 消费者信息
        //XInfoConsumers consumers(K key, String group);
        //// 消费者信息
        //XInfoGroups groups(K key);
        //// stream信息
        //XInfoStream info(K key);
        //
        //// 获取消费者组，消费者中未确认的消息
        //PendingMessagesSummary pending(K key, String group);
        //PendingMessages pending(K key, Consumer consumer)
        //PendingMessages pending(K key, String group, Range<?> range, long count)
        //PendingMessages pending(K key, String group, Range<?> range, long count)


        public void receive(Executor executor,
                            int batchSize,
                            StreamListener listener) {
            // 创建配置对象
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions<java.lang.String, MapRecord<java.lang.String, java.lang.String, java.lang.String>> streamMessageListenerContainerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                    // 一次性最多拉取多少条消息
                    .batchSize(batchSize)
                    // 执行消息轮询的执行器
                    .executor(executor)
                    // 消息消费异常处理器
                    .errorHandler(t -> {
                        throw new RuntimeException(t);
                    })
                    // 超时时间
                    // 设置为0，表示不超时，但超时后会抛出异常
                    .pollTimeout(Duration.ZERO)
                    // 序列化器
                    .serializer(new StringRedisSerializer())
                    .build();

            // 根据配置对象创建监听容器对象
            StreamMessageListenerContainer<java.lang.String, MapRecord<java.lang.String, java.lang.String, java.lang.String>> streamMessageListenerContainer = StreamMessageListenerContainer.create(redisTemplate.getConnectionFactory(), streamMessageListenerContainerOptions);

            // 使用监听容器对象开始监听消费（使用的是手动确认方式）
            streamMessageListenerContainer.receive(
                    //Consumer.from("group-1", "consumer-1"),
                    StreamOffset.create(key, ReadOffset.lastConsumed()),
                    message -> {
                        if (listener.onMessage(message)) {
                            // 手动确认消息
                            streamOperations.acknowledge(key, message);
                        }
                    });

            // 启动监听
            streamMessageListenerContainer.start();
        }
    }

    public static interface StreamListener {
        boolean onMessage(MapRecord<java.lang.String, java.lang.String, java.lang.String> message);
    }

    /**
     * RedisJSON 是一个 Redis 模块，它允许你在 Redis 中以原生 JSON 格式存储、操作和查询数据。
     */
    public static class Json {
    }

    public static interface Probabilistic {
    }

    public HyperLogLog HyperLogLog(java.lang.String key) {
        return new HyperLogLog(redisTemplate, key);
    }

    /**
     * HyperLogLog is a probabilistic data structure that estimates the cardinality of a set.
     *
     * 前言
     * 在数字世界中，了解“有多少独特”的问题比看起来要复杂得多。无论是计算一个网站的独立访客数，还是分析一个复杂事件的不同参与者，传统的方法往往既耗时又占空间。然而，有了Redis中的HyperLogLog，这一切都变得简单和高效。它通过一种巧妙的概率算法，使得我们可以用极小的空间来估算巨大数据集的基数。让我们一起揭开HyperLogLog的神秘面纱，看看它是如何在海量数据中找到独一无二的。
     *
     * HyperLogLog简介
     *
     * 基数和基数统计的重要性
     * • 基数的定义：在数学和数据分析中，基数（Cardinality）是指一个集合中不同元素的数量。在数据分析的语境下，它通常用来表示数据集中唯一元素的数量，如独立用户、独特事件类型等。
     * • 基数统计的应用：基数统计在许多领域都非常重要。例如，网站可能想知道有多少独立访客；社交网络可能需要统计有多少独特用户参与了某个话题；广告公司可能对接触到广告的不同用户数量感兴趣。
     * • 挑战：对于大数据集，传统的统计方法（如完全枚举）非常耗费资源，既慢又占用大量内存。因此，发展了一些概率算法来快速、近似地计算基数，这些算法在牺牲了一定的精度的同时大幅提高了效率。
     *
     * HyperLogLog的历史和革命性
     * • 历史：
     * • HyperLogLog算法是由Philippe Flajolet、Éric Fusy、Olivier Gandouet 和 Frédéric Meunier共同提出的。
     * • 它是基于之前的算法如LogLog和Linear Counting的基础上发展而来，旨在进一步减少空间占用并提高计算的速度和准确性。
     *
     * • 算法原理：
     * • HyperLogLog利用概率论中的哈希函数和调和平均数的概念来估计基数。
     * • 每个元素被哈希成一个二进制字符串。算法分析这些字符串的前导零的数量，使用这些信息来估计总体的基数。
     *
     * • 革命性：
     * • 空间效率：HyperLogLog可以使用极小的内存空间来估算非常大的基数，通常只需要几KB到1.5KB的空间，就可以处理亿级别的数据集。
     * • 高精度和低误差率：HyperLogLog提供了非常高的准确性，标准误差通常在0.81%左右，这对于大多数应用来说已经足够准确。
     * • 易于合并：不同HyperLogLog的统计结果可以很容易地合并在一起，这使得它非常适合分布式系统和并行计算。
     *
     * • 应用：
     * • 由于其卓越的性能和准确性，HyperLogLog已经被许多大型互联网公司广泛应用于数据基数统计，特别是在实时分析和大数据场景中。
     * 总的来说，HyperLogLog是基数估计领域的一次重大突破，它以其惊人的空间效率和高准确性解决了大规模数据集的基数统计问题，成为当代大数据技术栈中不可或缺的一部分。
     *
     * HyperLogLog的工作原理
     * HyperLogLog算法是一种高效的基数估计方法，用于快速、准确地估计一个大型数据集中的唯一元素数量。其基本原理涉及哈希函数、概率理论和对数计数等方面。
     *
     * 哈希函数
     * • 均匀分布：HyperLogLog算法首先将数据集中的每个元素通过哈希函数转换为一串伪随机的二进制字符串。理想的哈希函数应该保证输出的哈希值均匀分布，即每个二进制位上出现0和1的概率均等。
     * • 哈希值的作用：转换后的哈希值用于接下来的统计分析。这些值应该具有良好的随机性，以保证统计的准确性。
     *
     * 线性计数与对数计数
     * • 线性计数：最初的基数估计方法之一，适用于小型数据集。它使用一个位数组（bitmap）来跟踪观察到的每个不同哈希值。
     * • 对数计数：对于大型数据集，线性计数的空间需求变得不切实际。这时，对数计数方法就派上用场。LogLog计数和HyperLogLog计数都是对数计数方法的变体。
     *
     * HyperLogLog的核心算法
     * • 前导零的计数：HyperLogLog算法计算每个哈希值前导零的数量。更具体地说，它记录下每个哈希值中从左边起第一个1之前0的数量。
     * • 最大值的使用：对于每个哈希值，算法都会记录下最大的前导零的数量。这个最大值用于估计基数。
     * • 调和平均数：HyperLogLog利用这些最大前导零的调和平均数来估计基数。调和平均数对大数值的存在更为敏感，能更好地反映基数的大小。
     *
     * 概率和统计原理
     * • 概率模型：HyperLogLog算法基于概率模型来估计唯一元素的数量。前导零的数量可以被视为对数尺度上的距离估计，这个距离可以反映基数的大小。
     * • 基数的估算：通过统计学原理，可以从哈希值的前导零的分布推断出整个数据集的基数。HyperLogLog算法通过一系列数学推导将前导零的观察转换为对基数的估计。
     *
     * 注意事项
     * • 存储效率：HyperLogLog提供了非常高的存储效率，通常只需要12KB的存储空间就可以估计数亿个唯一元素。
     * • 精度：HyperLogLog只能提供近似值，其标准误差为0.81%。在大多数情况下，这种精度是可接受的。
     * • 无法获取元素：与集合不同，一旦元素被添加到HyperLogLog中，就无法再取出单独的元素。HyperLogLog只能告诉你大概有多少不同的元素。
     * 通过使用这些命令，你可以在Redis中轻松地实现高效的基数统计功能。无论是跟踪独立访客数量、统计唯一事件，还是合并多个统计结果，HyperLogLog都是一个极佳的选择。
     */
    /**
     * 应用场景
     * HyperLogLog由于其独特的性能和特性，适用于各种需要快速、大规模且近似统计唯一元素数量的场景。以下是一些典型的使用案例，以及HyperLogLog在这些场景中的优势和局限性。
     *
     * 网站访客统计
     * • 场景：网站希望统计独立访客的数量，通常通过唯一的访客标识（如IP地址）来计数。
     *
     * • 优势：
     * • 高效统计：HyperLogLog可以处理极大量的数据，提供快速响应，即使是在高流量的网站上也不例外。
     * • 节省空间：相比于存储每个独立访客的完整列表，HyperLogLog极大地节省了存储空间。
     *
     * • 局限性：
     * • 近似结果：HyperLogLog提供的是近似值，对于需要精确计数的场景可能不太适合。
     * ----
     *
     *社交网络的活跃用户估计
     * • 场景：社交网络平台希望估计在特定时间内活跃的独立用户数量。
     *
     * • 优势：
     * • 处理大数据集：社交网络通常有大量的用户活动数据，HyperLogLog能够高效地处理这些数据。
     * • 实时分析：可以实时更新HyperLogLog结构，快速获取最新的估计结果。
     *
     * • 局限性：
     * • 无法获取具体数据：HyperLogLog无法提供具体活跃的用户列表，只能给出数量的估计。
     * ----
     *
     *实时事件跟踪
     * • 场景：实时跟踪和统计系统中发生的唯一事件类型或操作，例如在大型分布式系统中跟踪不同类型的错误。
     *
     * • 优势：
     * • 快速响应：能够即时处理和统计大量事件。
     * • 易于扩展：适合分布式环境，可以通过合并多个HyperLogLog来统计整个分布式系统的事件。
     *
     * • 局限性：
     * • 精度问题：对于小数据集或需要非常精确结果的场景，HyperLogLog可能不是最佳选择。
     *
     *优势和局限性总结
     * 优势
     * • 高效的空间利用：使用极小的内存空间处理和估算大规模数据集的基数。
     * • 快速的计算速度：提供实时或近实时的基数估算，适合动态和高速变化的数据。
     * • 易于合并：可以轻松合并多个HyperLogLog结构，适合分布式系统和并行处理。
     * 局限性
     * • 近似而非精确：HyperLogLog只能提供基数的近似估计，有固定的标准误差。
     * • 无法提供具体元素：无法获取或重建被统计元素的具体信息，只能知道大概有多少不同的元素。
     * • 对小数据集敏感：在数据量较小的情况下，误差百分比可能较高，不如其他精确计数方法有效。
     * 在选择是否使用HyperLogLog时，应根据具体的应用场景和需求权衡这些优势和局限性。对于需要处理大规模数据集并且可以接受一定误差的场景，HyperLogLog是一个非常有吸引力的选择。
     * 优势和局限
     * 优势
     * 1. 空间效率：
     * • 低内存需求：HyperLogLog可以使用极小的内存空间来估算非常大的数据集的基数。通常，一个HyperLogLog结构只需要大约12KB的内存，即可处理亿级别的唯一元素。
     * • 可扩展性：对于大型分布式系统，HyperLogLog的空间效率使其成为监测基数的理想选择，因为它不会随着数据量的增加而线性增长内存消耗。
     * 2. 计算速度：
     * • 快速更新和查询：HyperLogLog支持快速添加元素和快速计算基数。它特别适合于需要实时或近实时统计的应用场景。
     * • 适合大数据集：对于大规模数据集，HyperLogLog能够提供快速的基数估算，这在传统的全量统计方法中几乎是不可能的。
     * 局限性
     * 1. 精度和错误率：
     * • 近似估算：HyperLogLog提供的是基数的近似估算。虽然对于大多数实际应用来说已经足够准确，但它不是一个精确计数器。在标准配置下，HyperLogLog的标准误差大约为0.81%。
     * • 小数据集敏感性：对于较小的数据集，HyperLogLog的误差百分比可能较高。在这种情况下，精确计数方法可能更为适合。
     * 2. 无法提供具体元素：
     * • HyperLogLog只能告诉你大约有多少不同的元素，但它不能告诉你具体是哪些元素。如果你需要知道具体的唯一元素列表，HyperLogLog则不适用。
     * 与传统方法的对比
     * 1. 集合：
     * • 精确计数：使用集合（如HashSet）可以精确地计数，但它们在处理大数据集时会消耗大量的内存。
     * • 适用性：对于小数据集或需要精确结果的场景，集合是一个好的选择。但对于大规模数据集，集合的空间效率远不如HyperLogLog。
     * 2. 计数排序和Bitmap：
     * • 中等数据集：对于中等规模的数据集，计数排序或Bitmap可能是合适的选择，它们提供了比集合更好的空间效率，但仍然不如HyperLogLog。
     * • 精确度和速度：这些方法通常可以提供精确的计数结果，但在处理极大规模数据时，它们的速度和空间效率不及HyperLogLog。
     *
     */

    /**
     * Redis数据类型之HyperLogLog（基数统计）：用于估算集合中不重复元素的数量，占用固定空间，适用于大规模数据的基数统计。
     * <p>
     * 当需要做大量数据统计时，普通的集合类型已经不能满足我们的需求了，这个时候我们可以借助 Redis 2.8.9 中提供的 HyperLogLog 来统计，它的优点是只需要使用 12k 的空间就能统计 2^64 的数据，
     * 但它的缺点是存在 0.81% 的误差，HyperLogLog 提供了三个操作方法 pfadd 添加元素、pfcount 统计元素和 pfmerge 合并元素。
     * <p>
     * <p>
     * <p>
     * HyperLogLog 是一种概率性数据结构，主要用于在空间效率非常高的情况下估算集合的基数（即不重复元素的数量）。
     * 它的主要特性是能够使用极少的内存来处理和估算大规模数据集的不重复元素数量，因此在许多实际应用场景中非常有用。
     * <p>
     * 以下是一些常见的 HyperLogLog 使用场景：
     * 1. 网站独立访客统计（UV）
     * 在网站分析中，统计独立访客（Unique Visitors，UV）是一个常见需求。传统方法需要存储每个访问者的唯一标识（如IP或用户ID），这会消耗大量内存。使用 HyperLogLog，可以在内存占用很低的情况下估算出独立访客数量。
     * <p>
     * 2. 去重后的用户行为计数
     * 对于电商平台、社交网络等应用，去重后的用户行为（如点击、点赞、分享等）计数是常见需求。例如，统计某个商品被多少不同的用户点击过，或者某篇文章被多少不同的用户点赞过。HyperLogLog 可以有效地解决这种去重计数问题。
     * <p>
     * 3. 实时数据流分析
     * 在实时数据流分析中，例如日志处理、监控系统等，需要对大量数据进行实时去重计数。HyperLogLog 能够在低延迟和低内存占用的情况下完成这些任务，非常适合这种高吞吐量要求的场景。
     * <p>
     * 4. 分布式系统中的去重计数
     * 在分布式系统中，需要从多个节点收集数据并进行去重计数。例如，从多个服务器节点收集用户行为数据，计算总的独立用户数量。HyperLogLog 允许将多个 HyperLogLog 合并，从而方便地实现分布式去重计数。
     * <p>
     * 5. 广告去重计费
     * 在广告投放系统中，需要统计去重后的广告展示次数（Impressions）和点击次数（Clicks），以便进行准确的计费。HyperLogLog 提供了一种高效的方法来估算去重后的展示和点击计数。
     * <p>
     * 6. 数据库记录去重统计
     * 在数据库中，有时需要统计表中某个字段的去重值数量，例如统计用户表中不同国家的用户数量。直接遍历整个表进行去重计数在大数据量情况下开销巨大，而 HyperLogLog 可以提供一种更高效的方法。
     * <p>
     * 7. 大数据集的快速去重计数
     * 在大数据分析中，快速估算海量数据集的不重复元素数量是一项常见需求。HyperLogLog 可以在处理 TB 级别的数据时保持较低的内存占用和计算开销。
     * <p>
     * 总结
     * HyperLogLog 在需要高效地估算大规模数据的基数（去重计数）时非常有用，特别是在以下几个方面表现突出：
     * <p>
     * 内存占用极小（通常只需要 12KB 左右）。
     * 支持快速插入和查询操作。
     * 适合分布式环境下的数据合并及去重计数。
     * 虽然 HyperLogLog 是一种概率性数据结构，估算结果可能会有轻微偏差，但在大量数据下这种偏差通常是可以接受的。因此，在很多实际应用场景中，HyperLogLog 提供了一种高效且实用的解决方案。
     * <p>
     * <p>
     * <p>
     * 使用场景：在线用户数、统计访问量等
     */
    public static class HyperLogLog implements Probabilistic {
        private HyperLogLogOperations<java.lang.String, Object> hyperLogLogOperations;
        private java.lang.String key;

        private HyperLogLog(RedisTemplate<java.lang.String, Object> redisTemplate, java.lang.String key) {
            this.hyperLogLogOperations = redisTemplate.opsForHyperLogLog();
            this.key = key;
        }

        /**
         * 向 HyperLogLog 添加元素
         *
         * @param values
         * @return
         */
        public Long add(Object... values) {
            return hyperLogLogOperations.add(key, values);
        }

        /**
         * 获取 HyperLogLog 的基数估算值
         *
         * @return
         */
        public Long size() {
            return hyperLogLogOperations.size(key);
        }

        /**
         * 合并多个 HyperLogLog 到目标 HyperLogLog
         *
         * @param destination 目标 HyperLogLog 的键
         * @param sourceKeys  源 HyperLogLog 的键
         */
        public Long union(java.lang.String destination, java.lang.String... sourceKeys) {
            return hyperLogLogOperations.union(destination, sourceKeys);
        }
    }

    /**
     * Bloom filter
     * Bloom filters are a probabilistic data structure that checks for presence of an element in a set.
     * <p>
     * Redis 提供了 Bloom filter 的实现作为其插件之一（通过 RedisBloom 模块），可以直接在 Redis 服务器上使用，因此不需要单独安装。你只需确保 Redis 服务器运行并加载了 RedisBloom 模块。
     */
    public static class BloomFilter implements Probabilistic {
    }

    /**
     * Cuckoo filter
     * Cuckoo filters are a probabilistic data structure that checks for presence of an element in a set.
     * <p>
     * Cuckoo filter 通常作为数据结构库的一部分提供，例如在 C++、Java 或 Python 的库中。
     * 你可以将这些库作为依赖添加到你的项目中，而不必安装到系统级环境中。
     */
    public static class CuckooFilter implements Probabilistic {
    }

    /**
     * t-digest
     * t-digest is a probabilistic data structure that allows you to estimate the percentile of a data stream.
     * <p>
     * t-digest 是一种用于近似数据分布的算法，它的实现通常作为软件库提供，例如 Java 中的 tdigest 库或 Python 中的 tdigest 库。
     * 你可以通过包管理工具将其集成到你的项目中，而不必安装到系统级环境中。
     */
    public static class TDigest implements Probabilistic {
    }

    /**
     * Top-K
     * Top-K is a probabilistic data structure that allows you to find the most frequent items in a data stream.
     * <p>
     * Top-K 查询和算法通常是作为查询模式或算法实现的一部分，可以直接集成到你的应用程序中，无需额外安装。
     * 例如，在数据库查询或数据处理过程中，你可以直接实现 Top-K 查询或算法，而不需要安装额外的软件。
     */
    public static class TopK implements Probabilistic {
    }

    /**
     * Count-min sketch
     * Count-min sketch is a probabilistic data structure that estimates the frequency of an element in a data stream.
     * <p>
     * Count-min sketch 是一种数据结构和算法，可以在多个编程语言中作为库使用，例如 Python 的 datasketch 库或 Java 的 java.util.sketch。
     * 你可以通过依赖管理工具（如 pip 或 Maven）将这些库添加到你的项目中，而无需将其安装到系统环境中。
     */
    public static class CountMinSketch implements Probabilistic {
    }

    /**
     * RedisTimeSeries 是一个 Redis 模块，专门用于处理时间序列数据，它提供了更高级别的操作和优化。首先，你需要安装 RedisTimeSeries 模块。
     * <p>
     * 安装 RedisTimeSeries 模块：
     * 你可以使用 Redis 的 MODULE LOAD 命令来加载 RedisTimeSeries 模块，或者在 Redis 启动时通过配置文件加载模块。
     */
    public static class TimeSeries {
    }

    // 发布/订阅（Pub/Sub）：一种消息通信模式，允许客户端订阅消息通道，并接收发布到该通道的消息。

    /**
     * 向指定通道发送消息
     * 一旦发布者发布了一条消息，所有订阅了该通道的订阅者都会接收到这条消息
     *
     * @param channel 通道，发布/订阅（Pub/Sub）模式通过通道进行消息传递，发布者发布消息到一个或多个通道
     * @param message
     */
    public Long publish(java.lang.String channel, Object message) {
        return redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 订阅一个或多个通道，以接收发布者发送到这些通道的消息
     * 一旦订阅了某个通道，订阅者将持续接收该通道上的消息
     *
     * @param messageListener 消息监听器
     * @param channels        通道，发布/订阅（Pub/Sub）模式通过通道进行消息传递，订阅者订阅一个或多个通道以接收相关消息
     */
    public void subscribe(MessageListener messageListener, java.lang.String... channels) {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.subscribe(messageListener, Arrays.stream(channels).map(java.lang.String::getBytes).toArray(byte[][]::new));
    }

    /**
     * 模式订阅（Pattern Subscribe）
     * 除了普通的通道订阅外，Redis还支持模式订阅，即可以通过订阅模式来匹配多个通道
     * 例如，订阅模式为 "chat:*" 的订阅者将接收到所有以 "chat:" 开头的通道上的消息
     *
     * @param messageListener
     * @param patterns
     */
    public void pSubscribe(MessageListener messageListener, java.lang.String... patterns) {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.pSubscribe(messageListener, Arrays.stream(patterns).map(java.lang.String::getBytes).toArray(byte[][]::new));
    }

    public Lock Lock(java.lang.String key) {
        return new Lock(redissonClient, key);
    }

    /**
     * Redis分布式锁
     * <p>
     * 分布式锁：控制分布式系统不同进程共同访问共享资源的一种锁的实现。如果不同的系统或同一个系统的不同主机之间共享了某个临界资源，往往需要互斥来防止彼此干扰，以保证一致性。
     * <p>
     * 分布式锁特征：
     * 1、互斥性: 任意时刻，只有一个客户端能持有锁
     * 2、锁超时释放：持有锁超时，可以释放，防止不必要的资源浪费，也可以防止死锁
     * 3、可重入性：一个线程如果获取了锁之后，可以再次对其请求加锁
     * 4、高性能和高可用：加锁和解锁需要开销尽可能低，同时也要保证高可用，避免分布式锁失效
     * 5、安全性：锁只能被持有的客户端删除，不能被其他客户端删除
     * <p>
     * 多机实现的分布式锁 Redisson + RedLock ?
     */
    public static class Lock {

        private RLock rLock;

        private Lock(RedissonClient redissonClient, java.lang.String key) {
            this.rLock = redissonClient.getLock(key);
        }

        /**
         * 阻塞式获取锁
         * 如果锁已被其他线程或客户端持有，则当前线程将被阻塞，直到获取到锁为止
         */
        public void lock() {
            rLock.lock();
        }

        /**
         * 阻塞式获取锁
         * 如果锁已被其他线程或客户端持有，则当前线程将被阻塞，直到获取到锁为止
         *
         * @param leaseTime 持有锁时间
         */
        public void lock(Duration leaseTime) {
            rLock.lock(leaseTime.getSeconds(), TimeUnit.SECONDS);
        }

        /**
         * 尝试非阻塞式获取锁
         * 如果锁当前没有被任何线程或客户端持有，则立即获取锁并返回true，否则返回false
         *
         * @return
         */
        public boolean tryLock() {
            return rLock.tryLock();
        }

        /**
         * 尝试在指定的等待时间内非阻塞式获取锁
         * 如果在等待时间内获取到了锁，则返回true，否则返回false
         *
         * @param waitTime 最多等待锁时间
         * @return
         * @throws InterruptedException
         */
        public boolean tryLock(Duration waitTime) throws InterruptedException {
            return rLock.tryLock(waitTime.getSeconds(), TimeUnit.SECONDS);
        }

        /**
         * 尝试在指定的等待时间内非阻塞式获取锁
         * 如果在等待时间内获取到了锁，则返回true，否则返回false
         *
         * @param waitTime  最多等待锁时间
         * @param leaseTime 持有锁时间
         */
        public boolean tryLock(Duration waitTime, Duration leaseTime) throws InterruptedException {
            return rLock.tryLock(waitTime.getSeconds(), leaseTime.getSeconds(), TimeUnit.SECONDS);
        }

        /**
         * 释放锁
         * 如果当前线程持有锁，则释放锁，否则抛出 {@link IllegalMonitorStateException} 异常
         */
        public void unlock() {
            rLock.unlock();
        }

        /**
         * 强制释放锁，不管当前线程是否持有锁，都会释放
         */
        public void forceUnlock() {
            rLock.forceUnlock();
        }
    }

    public static RedisConnectionFactory createRedisConnectionFactory(java.lang.String host, int port) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public static RedisTemplate<java.lang.String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<java.lang.String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<?> valueSerializer = new GenericToStringSerializer<>(Object.class);

        // 使用 Jackson 提供的通用 Serializer
//        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(JsonUtil.OBJECT_MAPPER);

        // String 类型的 key/value 序列化器
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(valueSerializer);

        // Hash 类型的 key/value 序列化器
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    public static Redis create(java.lang.String host, int port) {
        RedisConnectionFactory redisConnectionFactory = createRedisConnectionFactory(host, port);
        RedisTemplate<java.lang.String, Object> redisTemplate = createRedisTemplate(redisConnectionFactory);
        return new Redis(redisTemplate, null);
    }

    public static Redis create() {
        return create("localhost", 6379);
    }

}
