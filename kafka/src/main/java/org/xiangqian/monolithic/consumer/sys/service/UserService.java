package org.xiangqian.monolithic.consumer.sys.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:21 2024/06/26
 */
public interface UserService {

    String INFO = "^sys-user-([^-]+)-info$";
    String EVENT = "^sys-user-([^-]+)-event$";

    void info(ConsumerRecord<String, String> record);

    void event(List<ConsumerRecord<String, String>> records);

}
