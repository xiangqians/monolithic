package org.xiangqian.monolithic.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 19:43 2024/06/26
 */
public class Consumer implements Closeable {

    private org.apache.kafka.clients.consumer.Consumer<String, String> consumer;

    private Consumer(org.apache.kafka.clients.consumer.Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    /**
     * 订阅主题集合
     *
     * @param topics
     */
    public void subscribe(Set<String> topics) {
        consumer.subscribe(topics);
    }

    /**
     * 订阅主题集合
     *
     * @param topics
     */
    public void subscribe(String... topics) {
        subscribe(Arrays.stream(topics).collect(Collectors.toSet()));
    }

    /**
     * 从 Kafka Broker 中拉取消息，如果没有可用的消息，它会等待指定的时间（由参数 timeout 指定），直到有消息到达或者超时
     *
     * @param timeout 指定拉取消息的超时时间。如果在指定时间内没有消息到达，方法会返回一个空的记录集合
     * @return
     */
    public ConsumerRecords<String, String> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    /**
     * 手动同步提交当前消费者组的指定分区的偏移量
     *
     * @param record
     */
    public void commitSync(ConsumerRecord<String, String> record) {
        consumer.commitSync(Map.of(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1)));
    }

    /**
     * 手动异步提交当前消费者组的所有分区的偏移量
     * 相较于同步提交，异步提交通常更适合在处理大量消息时，以减少等待时间并提高整体吞吐量。
     */
    public void commitAsync() {
        consumer.commitAsync();
    }


    @Override
    public void close() throws IOException {
        consumer.close();
    }

    public static String toString(ConsumerRecord<String, String> record) {
        return String.format("Received Message" +
                        "\n\tTopic\t\t: %s" +
                        "\n\tPartition\t: %s" +
                        "\n\tOffset\t\t: %s" +
                        "\n\tKey\t\t\t: %s" +
                        "\n\tValue\t\t: %s",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value());
    }

    /**
     * 创建消费者
     *
     * @param bootstrapServers Kafka服务器地址和端口
     * @param groupId          消费者组 ID。确保每个消费者都属于同一个消费者组，以便实现消息的分发和负载均衡。
     * @param offset           Kafka 主题中的每个分区都有一个偏移量，用于跟踪消费者已经处理到的消息位置。
     *                         当消费者启动或者发生了一些异常情况（例如消费者组没有偏移量或偏移量无效），就需要根据 {@link org.apache.kafka.clients.consumer.ConsumerConfig#AUTO_OFFSET_RESET_CONFIG} 来决定从何处开始消费消息。
     * @param autoCommit       是否自动提交偏移量
     *                         当 true 时，消费者会周期性地自动提交偏移量到 Kafka 服务器。
     *                         当 false 时，消费者不会自动提交偏移量，需要应用程序手动调用确认偏移量的方法来提交偏移量。
     * @return
     */
    public static Consumer create(String bootstrapServers, String groupId, Offset offset, boolean autoCommit) {
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset.getValue(),
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit));
        return new Consumer(consumerFactory.createConsumer());
    }

}
