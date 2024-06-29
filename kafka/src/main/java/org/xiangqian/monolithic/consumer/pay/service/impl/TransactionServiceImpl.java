package org.xiangqian.monolithic.consumer.pay.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.consumer.DefaultKafka;
import org.xiangqian.monolithic.consumer.pay.service.TransactionService;

/**
 * @author xiangqian
 * @date 19:24 2024/06/26
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Override
    public void create(ConsumerRecord<String, String> record) {
        log.debug("接收到消息 {}", DefaultKafka.toString(record));
    }

    @Override
    public void callback(ConsumerRecord<String, String> record) {
        log.debug("接收到消息 {}", DefaultKafka.toString(record));
    }

    @Override
    public void refund(ConsumerRecord<String, String> record) {
        log.debug("接收到消息 {}", DefaultKafka.toString(record));
    }

}
