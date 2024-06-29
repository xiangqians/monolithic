package org.xiangqian.monolithic.consumer.sys.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.util.RegexUtil;
import org.xiangqian.monolithic.consumer.DefaultKafka;
import org.xiangqian.monolithic.consumer.sys.service.UserService;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:22 2024/06/26
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public void info(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String[] values = RegexUtil.extractValues("^sys-user-([^-]+)-info$", topic);
        String userId = values[0];
        log.debug("userId = {}", userId);
        log.debug("接收到消息 {}", DefaultKafka.toString(record));
    }

    @Override
    public void event(List<ConsumerRecord<String, String>> records) {

    }

}
