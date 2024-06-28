package org.xiangqian.monolithic.emqx.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.xiangqian.monolithic.emqx.Subscribe;

/**
 * @author xiangqian
 * @date 21:20 2024/06/28
 */
@Subscribe(topicFilter = "/sys", qos = 0)
public interface SysService extends IMqttMessageListener {

}
