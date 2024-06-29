package org.xiangqian.monolithic.consumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.consumer.pay.service.TransactionService;

/**
 * @author xiangqian
 * @date 19:27 2024/06/26
 */
@Component
public class PayListener {

    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topicPattern = TransactionService.CREATE)
    public void transactionCreate(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        // 处理消息业务逻辑
        transactionService.create(record);

        // 手动提交偏移量，消息确认
        acknowledgment.acknowledge();
    }

    @KafkaListener(topicPattern = TransactionService.CALLBACK)
    public void transactionCallback(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        // 处理消息业务逻辑
        transactionService.callback(record);

        // 手动提交偏移量，消息确认
        acknowledgment.acknowledge();
    }

    @KafkaListener(topicPattern = TransactionService.REFUND)
    public void transactionRefund(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        // 处理消息业务逻辑
        transactionService.refund(record);

        // 手动提交偏移量，消息确认
        acknowledgment.acknowledge();
    }

}
