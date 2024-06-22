package org.xiangqian.monolithic.common.redis;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Redis Streams 是 Redis 5.0 引入的数据结构，适用于处理消息队列和日志等应用场景。
 *
 * @author xiangqian
 * @date 17:24 2024/06/22
 */
public class RedisStream {

    private RedisTemplate<String, Object> redisTemplate;
    private StreamOperations<String, String, String> streamOperations;
    private String key;

    RedisStream(RedisTemplate<String, Object> redisTemplate, String key) {
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
    public String createGroup(String groupName, ReadOffset readOffset) {
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
    public StreamInfo.XInfoConsumers getConsumers(String groupName) {
        return streamOperations.consumers(key, groupName);
    }

    /**
     * 获取消费者组中挂起的消息
     *
     * @param groupName 消费者组名称
     * @return
     */
    public PendingMessagesSummary pending(String groupName) {
        return streamOperations.pending(key, groupName);
    }

    /**
     * 获取指定消费者在消费者组中挂起的消息
     *
     * @param groupName    消费者组名称
     * @param consumerName 消费者名称
     * @return
     */
    public PendingMessages pending(String groupName, String consumerName) {
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
    public PendingMessages pending(String groupName, Range<?> range, long count) {
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
    public PendingMessages pending(String groupName, String consumerName, Range<?> range, long count) {
        return streamOperations.pending(key, Consumer.from(groupName, consumerName), range, count);
    }

    /**
     * 删除消费者组中的消费者
     *
     * @param groupName    消费者组名称
     * @param consumerName 消费者名称
     * @return
     */
    public Boolean deleteConsumer(String groupName, String consumerName) {
        return streamOperations.deleteConsumer(key, Consumer.from(groupName, consumerName));
    }

    /**
     * 销毁一个消费者组
     *
     * @param groupName 消费者组名称
     * @return
     */
    public Boolean destroyGroup(String groupName) {
        return streamOperations.destroyGroup(key, groupName);
    }

    /**
     * 向流中添加一条记录
     *
     * @param map 要添加的值
     * @return
     */
    public RecordId add(Map<String, String> map) {
        return streamOperations.add(MapRecord.create(key, map));
    }

    //read(StreamReadOptions readOptions, StreamOffset<K>... streams)
    //从一个或多个流中读取记录。
    //StreamReadOptions readOptions: 读取选项配置（如块时间、消息计数等）。
    //StreamOffset<K>... streams: 要读取的流和偏移量。

    public List<MapRecord<String, String, String>> read(String groupName, String consumerName) {
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
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> streamMessageListenerContainerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
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
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer = StreamMessageListenerContainer.create(redisTemplate.getConnectionFactory(), streamMessageListenerContainerOptions);

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

    public static interface StreamListener {
        boolean onMessage(MapRecord<String, String, String> message);
    }

}
