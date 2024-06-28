package org.xiangqian.monolithic.emqx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.emqx.Emqx;
import org.xiangqian.monolithic.emqx.service.PayService;

import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 23:40 2024/06/28
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("接收到主题 {} 消息 {}", topic, Emqx.toString(mqttMessage));
    }

}
