package org.xiangqian.monolithic.emqx.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.xiangqian.monolithic.common.emqx.Qos;
import org.xiangqian.monolithic.emqx.Subscriber;

/**
 * @author xiangqian
 * @date 21:20 2024/06/28
 */
@Subscriber(topicFilter = "sys/#", qos = Qos.AT_LEAST_ONCE)
public interface SysService extends IMqttMessageListener {

}
