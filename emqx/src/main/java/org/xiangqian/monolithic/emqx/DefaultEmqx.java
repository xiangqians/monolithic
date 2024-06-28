package org.xiangqian.monolithic.emqx;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.xiangqian.monolithic.common.emqx.Emqx;
import org.xiangqian.monolithic.common.emqx.EmqxProperties;
import org.xiangqian.monolithic.common.util.ResourceUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xiangqian
 * @date 21:25 2024/06/28
 */
@Slf4j
public class DefaultEmqx extends Emqx {

    @Autowired
    private ApplicationContext applicationContext;

    public DefaultEmqx(EmqxProperties emqxProperties) {
        super(emqxProperties);
    }

    @Override
    @SneakyThrows
    protected void connect(MqttConnectOptions mqttConnectOptions) throws MqttException {
        super.connect(mqttConnectOptions);

        // 订阅主题
        String basePkg = this.getClass().getPackageName() + ".**";
        Set<Class<?>> classes = ResourceUtil.getClasses(basePkg);
        List<String> topicFilters = new ArrayList<>(8);
        List<Integer> qoss = new ArrayList<>(8);
        List<IMqttMessageListener> mqttMessageListeners = new ArrayList<>(8);
        for (Class<?> clazz : classes) {
            int modifiers = clazz.getModifiers();
            Subscribe subscribe = null;
            if (Modifier.isPublic(modifiers)
                    && Modifier.isInterface(modifiers)
                    && IMqttMessageListener.class.isAssignableFrom(clazz)
                    && (subscribe = clazz.getAnnotation(Subscribe.class)) != null) {
                try {
                    IMqttMessageListener mqttMessageListener = (IMqttMessageListener) applicationContext.getBean(clazz);

                    String topicFilter = subscribe.topicFilter();
                    topicFilters.add(topicFilter);

                    int qos = subscribe.qos();
                    qoss.add(qos);

                    mqttMessageListeners.add(mqttMessageListener);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(topicFilters)) {
            mqttClient.subscribe(topicFilters.toArray(String[]::new), qoss.stream().mapToInt(Integer::intValue).toArray(), mqttMessageListeners.toArray(IMqttMessageListener[]::new));
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        super.messageArrived(topic, mqttMessage);
    }

}
