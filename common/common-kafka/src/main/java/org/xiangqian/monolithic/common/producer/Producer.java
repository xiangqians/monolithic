package org.xiangqian.monolithic.common.producer;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 生产者
 *
 * @author xiangqian
 * @date 12:02 2024/06/22
 */
public class Producer implements Closeable {

    private KafkaTemplate<String, String> kafkaTemplate;

    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 异步发送消息
     *
     * @param topic 指定要发送消息的目标主题（topic）
     * @param key   消息的键（可以为 null）
     * @param value 要发送的消息（可以为 null）
     * @return
     */
    public CompletableFuture<SendResult<String, String>> send(String topic, String key, String value) {
        return kafkaTemplate.send(topic, key, value);
    }

    public CompletableFuture<SendResult<String, String>> send(String topic, String value) {
        return kafkaTemplate.send(topic, value);
    }

    @Override
    public void close() throws IOException {
        kafkaTemplate.destroy();
    }

    public static Producer create() {
        return create("localhost:9092");
    }

    public static Producer create(String bootstrapServers) {
        // Kafka 生产者工厂配置
        Map<String, Object> configs = new HashMap<>();
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);

        // KafkaTemplate 实例
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        return new Producer(kafkaTemplate);
    }

}
