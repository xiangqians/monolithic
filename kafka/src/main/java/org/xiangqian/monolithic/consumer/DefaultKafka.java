package org.xiangqian.monolithic.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.xiangqian.monolithic.common.kafka.Kafka;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消费者（Consumer）
 * 消费者是消息的接收者，它从消息队列中订阅消息并进行处理。消费者可以订阅一个或多个主题或队列，并根据需要处理接收到的消息。
 * <p>
 * 消费者组（Consumer Group）
 * 消费者组是一组逻辑上相关联的消费者实例的集合。每个消费者组都有一个唯一的组ID，并且每个消费者组内的消费者实例共同消费订阅主题或队列中的消息。
 * <p>
 * 确认机制（Acknowledgment）
 * 确认机制是指消费者接收并处理消息后向消息队列发送确认的过程。这个过程确保了消息在处理时的可靠性和一致性。
 * <p>
 * 注意事项：
 * 1、确保处理完消息后再做消息commit，避免业务消息处理失败，无法重新拉取处理失败的消息。
 * 2、Kafka不能保证消费重复的消息，业务侧需保证消息处理的幂等性。
 * 3、auto.commit.enable需设置为false，避免在消息消费失败但因commit而使offset更新而导致消息丢失。
 * 4、consumer不能频繁加入和退出group，频繁加入和退出，会导致consumer频繁做重平衡（Rebalance），阻塞消费。
 * 5、consumer数量不能超过topic分区数，否则会有consumer拉取不到消息。
 *
 * @author xiangqian
 * @date 19:43 2024/06/26
 */
public class DefaultKafka extends Kafka {

    private Consumer<String, String> consumer;

    public DefaultKafka(KafkaTemplate<String, String> kafkaTemplate, Consumer<String, String> consumer) {
        super(kafkaTemplate);
        this.consumer = consumer;
    }

    /**
     * 订阅主题
     *
     * @param topics 主题，Kafka 主题名的官方命名规范建议使用小写字母、数字和减号（-）来命名主题。
     */
    public void subscribe(Set<String> topics) {
        consumer.subscribe(topics);
    }

    /**
     * 订阅主题
     *
     * @param topics 主题，Kafka 主题名的官方命名规范建议使用小写字母、数字和减号（-）来命名主题。
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
    public void destroy() throws Exception {
        try {
            super.destroy();
        } finally {
            if (consumer != null) {
                consumer.close();
                consumer = null;
            }
        }
    }

    public static String toString(ConsumerRecord<String, String> record) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\t").append("Topic\t: ").append(record.topic());
        builder.append("\n\t").append("Partition: ").append(record.partition());
        builder.append("\n\t").append("Offset\t: ").append(record.offset());
        builder.append("\n\t").append("Key\t\t: ").append(record.key());
        builder.append("\n\t").append("Message\t: ").append(record.value());
        return builder.toString();
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
    public static DefaultKafka create(String bootstrapServers, String groupId, Offset offset, boolean autoCommit) {
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset.getValue(),
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit));
        return new DefaultKafka(null, consumerFactory.createConsumer());
    }

}
