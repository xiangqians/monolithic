package org.xiangqian.monolithic.common.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 生产者（Producer）
 * 生产者是消息的发送者，它负责将消息发送到消息队列中。生产者通常与消息队列进行交互，将消息发布到特定的主题或队列中。
 *
 * @author xiangqian
 * @date 12:02 2024/06/22
 */
public class Kafka implements DisposableBean {

    private KafkaTemplate<String, String> kafkaTemplate;

    public Kafka(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 异步发送消息到队列中
     *
     * @param topic   主题，Kafka 主题名的官方命名规范建议使用小写字母、数字和减号（-）来命名主题。
     * @param message 消息
     * @return
     */
    public CompletableFuture<SendResult<String, String>> send(String topic, String message) {
        return kafkaTemplate.send(topic, message);
    }

    /**
     * 异步发送消息到队列中
     * <p>
     * {@link org.apache.kafka.clients.producer.KafkaProducer#doSend(org.apache.kafka.clients.producer.ProducerRecord, org.apache.kafka.clients.producer.Callback)}
     * {@link org.apache.kafka.clients.producer.KafkaProducer#partition(org.apache.kafka.clients.producer.ProducerRecord, byte[], byte[], org.apache.kafka.common.Cluster)}
     *
     * @param topic   主题，Kafka 主题名的官方命名规范建议使用小写字母、数字和减号（-）来命名主题。
     * @param key     消息的键，用于控制消息的分区逻辑。
     *                Kafka 将根据 key 进行哈希处理，以确保具有相同 key 的消息被发送到同一个分区。
     *                这种方式可以保证具有相同 key 的消息在消费时能够按照顺序处理，因为它们会被发送到同一个分区，并且分区内的消息是有序的。
     *                <p>
     *                需要注意的是，Kafka仅保证相同键的消息被分配到同一个分区，但不保证分区之间的顺序。
     *                即使共享相同键的消息被分配到不同分区，它们仍然可能以并行的方式被消费。
     *                因此，在某些特定情况下，当消息的严格顺序对您的应用程序很重要时，您需要使用只有一个分区的主题来保证严格的顺序性。
     * @param message 消息
     * @return
     */
    public CompletableFuture<SendResult<String, String>> send(String topic, String key, String message) {
        return kafkaTemplate.send(topic, key, message);
    }

    // Spring 容器销毁（即关闭）时，释放资源
    @Override
    public void destroy() throws Exception {
        if (kafkaTemplate != null) {
            kafkaTemplate.destroy();
            kafkaTemplate = null;
        }
    }

    public static Kafka create() {
        return create("localhost:9092");
    }

    public static Kafka create(String bootstrapServers) {
        // Kafka 生产者工厂配置
        Map<String, Object> configs = new HashMap<>();
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);

        // KafkaTemplate 实例
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        return new Kafka(kafkaTemplate);
    }

}
