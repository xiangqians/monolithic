package org.xiangqian.monolithic.emqx.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.xiangqian.monolithic.emqx.Qos;
import org.xiangqian.monolithic.emqx.Subscriber;

/**
 * @author xiangqian
 * @date 22:28 2024/06/28
 */
@Subscriber(topicFilter = "pay/#", qos = Qos.AT_LEAST_ONCE)
public interface PayService extends IMqttMessageListener {
}
