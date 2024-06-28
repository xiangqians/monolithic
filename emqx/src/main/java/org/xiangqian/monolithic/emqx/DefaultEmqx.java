package org.xiangqian.monolithic.emqx;

import lombok.Data;
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
import org.xiangqian.monolithic.common.util.RegexUtil;
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

    private List<SubscriberWrapper> subscriberWrappers;

    public DefaultEmqx(EmqxProperties emqxProperties) {
        super(emqxProperties);
    }

    @Override
    @SneakyThrows
    protected void connect(MqttConnectOptions mqttConnectOptions) throws MqttException {
        // 获取MQTT订阅者
        String basePkg = this.getClass().getPackageName() + ".**";
        Set<Class<?>> classes = ResourceUtil.getClasses(basePkg);
        subscriberWrappers = new ArrayList<>(10);
        for (Class<?> clazz : classes) {
            int modifiers = clazz.getModifiers();
            Subscriber subscribe = null;
            if (Modifier.isPublic(modifiers)
                    && Modifier.isInterface(modifiers)
                    && IMqttMessageListener.class.isAssignableFrom(clazz)
                    && (subscribe = clazz.getAnnotation(Subscriber.class)) != null) {
                try {
                    IMqttMessageListener mqttMessageListener = (IMqttMessageListener) applicationContext.getBean(clazz);
                    String topicFilter = subscribe.topicFilter();
                    int qos = subscribe.qos();
                    subscriberWrappers.add(new SubscriberWrapper(topicFilter, qos, mqttMessageListener));
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }

        // 连接
        super.connect(mqttConnectOptions);

        // 订阅主题
        if (CollectionUtils.isNotEmpty(subscriberWrappers)) {
            mqttClient.subscribe(subscriberWrappers.stream().map(SubscriberWrapper::getTopicFilter).toArray(String[]::new),
                    subscriberWrappers.stream().map(SubscriberWrapper::getQos).mapToInt(Integer::intValue).toArray(),
                    subscriberWrappers.stream().map(SubscriberWrapper::getMqttMessageListener).toArray(IMqttMessageListener[]::new));
        }
    }

    /**
     * 当设置 {@link MqttConnectOptions#setCleanSession(boolean)} 为 false 时，
     * 客户端断开、重连期间，如果有消息时，会将此期间的消息传递到客户端，
     * 所以，重写此方法便是处理此类数据
     *
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        for (SubscriberWrapper subscriberWrapper : subscriberWrappers) {
            if (RegexUtil.isMatch(subscriberWrapper.getTopicFilterRegex(), topic)) {
                subscriberWrapper.getMqttMessageListener().messageArrived(topic, mqttMessage);
            }
        }
    }

    @Data
    public static class SubscriberWrapper {
        private String topicFilter;
        private int qos;
        private IMqttMessageListener mqttMessageListener;

        // topicFilter正则表达式
        private String topicFilterRegex;

        public SubscriberWrapper(String topicFilter, int qos, IMqttMessageListener mqttMessageListener) {
            this.topicFilter = topicFilter;
            this.qos = qos;
            this.mqttMessageListener = mqttMessageListener;
            this.topicFilterRegex = "^" + topicFilter.replace("+", "([^/]+)").replace("#", "(.+)") + "$";
        }
    }

}
