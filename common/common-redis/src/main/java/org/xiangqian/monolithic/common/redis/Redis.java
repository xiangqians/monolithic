package org.xiangqian.monolithic.common.redis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    private RedisTemplate<String, Object> redisTemplate;
    private RedissonClient redissonClient;

    public Redis(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * 是否存在某个前缀的键
     *
     * @param prefix
     * @return
     */
    public boolean hasKeyWithPrefix(String prefix) {
        return CollectionUtils.isNotEmpty(getKeysWithPrefix(prefix, 1));
    }

    /**
     * 根据键前缀获取键集合
     *
     * @param prefix
     * @param count
     * @return
     */
    public Set<String> getKeysWithPrefix(String prefix, int count) {
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
     * 指定的键是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return BooleanUtils.toBoolean(redisTemplate.hasKey(key));
    }

    /**
     * 设置缓存键过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @return
     */
    public Boolean expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 删除缓存键
     *
     * @param key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除缓存键集合
     *
     * @param keys
     * @return
     */
    public Long delete(Set<String> keys) {
        return redisTemplate.delete(keys);
    }

    public RedisString String() {
        return RedisString.get(redisTemplate);
    }

    public RedisList List(String key) {
        return new RedisList(redisTemplate, key);
    }

    public RedisSet Set(String key) {
        return new RedisSet(redisTemplate, key);
    }

    public RedisSortedSet SortedSet(String key) {
        return new RedisSortedSet(redisTemplate, key);
    }

    public RedisHash Hash(String key) {
        return new RedisHash(redisTemplate, key);
    }

    public RedisBitmap Bitmap(String key) {
        return new RedisBitmap(redisTemplate, key);
    }

    public RedisBitField BitField(String key) {
        return new RedisBitField(redisTemplate, key);
    }

    public RedisGeospatial Geospatial(String key) {
        return new RedisGeospatial(redisTemplate, key);
    }

    public RedisStream Stream(String key) {
        return new RedisStream(redisTemplate, key);
    }

    public RedisJson json() {
        throw new UnsupportedOperationException();
    }

    public RedisHyperLogLog HyperLogLog(String key) {
        return new RedisHyperLogLog(redisTemplate, key);
    }

    public RedisBloomFilter BloomFilter() {
        throw new UnsupportedOperationException();
    }

    public RedisCuckooFilter CuckooFilter() {
        throw new UnsupportedOperationException();
    }

    public RedisTDigest TDigest() {
        throw new UnsupportedOperationException();
    }

    public RedisTopK TopK() {
        throw new UnsupportedOperationException();
    }

    public RedisCountMinSketch CountMinSketch() {
        throw new UnsupportedOperationException();
    }

    public RedisTimeSeries TimeSeries() {
        throw new UnsupportedOperationException();
    }

    // 发布/订阅（Pub/Sub）：一种消息通信模式，允许客户端订阅消息通道，并接收发布到该通道的消息。

    /**
     * 向指定通道发送消息
     * 一旦发布者发布了一条消息，所有订阅了该通道的订阅者都会接收到这条消息
     *
     * @param channel 通道，发布/订阅（Pub/Sub）模式通过通道进行消息传递，发布者发布消息到一个或多个通道
     * @param message
     */
    public Long publish(String channel, Object message) {
        return redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 订阅一个或多个通道，以接收发布者发送到这些通道的消息
     * 一旦订阅了某个通道，订阅者将持续接收该通道上的消息
     *
     * @param messageListener 消息监听器
     * @param channels        通道，发布/订阅（Pub/Sub）模式通过通道进行消息传递，订阅者订阅一个或多个通道以接收相关消息
     */
    public void subscribe(MessageListener messageListener, String... channels) {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.subscribe(messageListener, Arrays.stream(channels).map(String::getBytes).toArray(byte[][]::new));
    }

    /**
     * 模式订阅（Pattern Subscribe）
     * 除了普通的通道订阅外，Redis还支持模式订阅，即可以通过订阅模式来匹配多个通道
     * 例如，订阅模式为 "chat:*" 的订阅者将接收到所有以 "chat:" 开头的通道上的消息
     *
     * @param messageListener
     * @param patterns
     */
    public void pSubscribe(MessageListener messageListener, String... patterns) {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.pSubscribe(messageListener, Arrays.stream(patterns).map(String::getBytes).toArray(byte[][]::new));
    }

    public RedisLock Lock(String key) {
        return new RedisLock(redissonClient, key);
    }

    public static RedisConnectionFactory createRedisConnectionFactory(String host, int port) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public static RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

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

    public static Redis create(String host, int port) {
        RedisConnectionFactory redisConnectionFactory = createRedisConnectionFactory(host, port);
        RedisTemplate<String, Object> redisTemplate = createRedisTemplate(redisConnectionFactory);
        return new Redis(redisTemplate, null);
    }

    public static Redis create() {
        return create("localhost", 6379);
    }

}
