package org.xiangqian.monolithic.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.common.util.RegexUtil;
import org.xiangqian.monolithic.consumer.Consumer;
import org.xiangqian.monolithic.consumer.sys.service.RoleService;
import org.xiangqian.monolithic.consumer.sys.service.UserService;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:37 2024/06/26
 */
@Slf4j
@Component
public class SysListener {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 订阅用户主题
     * <p>
     * ^         : 匹配输入的开始位置
     * /sys/user/: 确切匹配字符串 "/sys/user/"
     * ([^/]+)   : 匹配任意字符（除了 /）一次或多次
     * $         : 表示字符串的结束
     * 以 /sys/user/ 开头，后面跟着任意字符序列（但不包括 /）
     *
     * @param record
     * @param acknowledgment 消息确认
     *                       {@link org.springframework.kafka.support.Acknowledgment#acknowledge()}
     *                       {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer.ConsumerAcknowledgment#acknowledge()}
     *                       {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#doAck(org.apache.kafka.clients.consumer.ConsumerRecord)}
     *                       {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#processAck(org.apache.kafka.clients.consumer.ConsumerRecord)}
     *                       {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#ackImmediate(org.apache.kafka.clients.consumer.ConsumerRecord)}
     *                       {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#commitSync(java.util.Map)} / {@link org.springframework.kafka.listener.KafkaMessageListenerContainer.ListenerConsumer#commitAsync(java.util.Map)}
     *                       {@link org.apache.kafka.clients.consumer.Consumer#commitSync(java.util.Map, java.time.Duration)} /  {@link org.apache.kafka.clients.consumer.Consumer#commitAsync(java.util.Map, org.apache.kafka.clients.consumer.OffsetCommitCallback)}
     */
    @KafkaListener(topicPattern = "^/sys/user/([^/]+)$")
    public void user(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String topic = record.topic();
        String[] values = RegexUtil.extractValues("^/sys/user/([^/]+)$", topic);
        String id = values[0];
        log.debug("【sys接收到消息】{}", Consumer.toString(record));
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "yourTopic", groupId = "yourGroupId")
    public void consume(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, String> record : records) {
            // 处理消息的业务逻辑
        }

        // 手动提交偏移量，实现批量确认
        acknowledgment.acknowledge();
    }

}
