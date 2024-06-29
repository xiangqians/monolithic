package org.xiangqian.monolithic.common.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class KafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Kafka.class)
    public Kafka kafka(KafkaTemplate<String, String> kafkaTemplate) {
        return new Kafka(kafkaTemplate);
    }

}
