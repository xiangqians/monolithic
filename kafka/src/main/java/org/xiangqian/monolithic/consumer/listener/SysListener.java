package org.xiangqian.monolithic.consumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.consumer.sys.service.RoleService;
import org.xiangqian.monolithic.consumer.sys.service.UserService;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:37 2024/06/26
 */
@Component
public class SysListener {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 订阅用户主题
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
    @KafkaListener(topicPattern = UserService.INFO)
    public void userInfo(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        // 处理消息业务逻辑
        userService.info(record);

        // 手动提交偏移量，消息确认
        acknowledgment.acknowledge();
    }

    @KafkaListener(topicPattern = UserService.EVENT)
    public void userEvent(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        // 处理消息业务逻辑
        userService.event(records);

        // 手动提交偏移量，批量消息确认
        acknowledgment.acknowledge();
    }

}
