package org.xiangqian.monolithic.common.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * {@link org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration}
 *
 * @author xiangqian
 * @date 23:40 2024/06/25
 */
@Configuration(proxyBeanMethods = false)
public class ProducerAutoConfiguration {

    @Bean
    public Producer kafka(KafkaTemplate<String, String> kafkaTemplate) {
        return new Producer(kafkaTemplate);
    }

}
