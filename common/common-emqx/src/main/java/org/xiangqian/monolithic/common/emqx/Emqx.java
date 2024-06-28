package org.xiangqian.monolithic.common.emqx;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 21:53 2024/06/28
 */
@Slf4j
public class Emqx implements MqttCallback, ApplicationRunner, DisposableBean {

    protected EmqxProperties emqxProperties;
    protected MqttClient mqttClient;

    public Emqx(EmqxProperties emqxProperties) {
        this.emqxProperties = emqxProperties;
    }

    protected MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        // 设置是否清除会话
        mqttConnectOptions.setCleanSession(false);

        // 设置心跳间隔
//        mqttConnectOptions.setKeepAliveInterval();

        // 设置连接超时时间
        mqttConnectOptions.setConnectionTimeout(10);

        // 认证用户名&密码
        mqttConnectOptions.setUserName(emqxProperties.getUser());
        mqttConnectOptions.setPassword(emqxProperties.getPasswd().toCharArray());

        // 设置是否自动重连
        mqttConnectOptions.setAutomaticReconnect(true);

        return mqttConnectOptions;
    }

    protected void connect(MqttConnectOptions mqttConnectOptions) throws MqttException {
        mqttClient.connect(mqttConnectOptions);
    }

    @Override
    public final void run(ApplicationArguments args) throws Exception {
        mqttClient = new MqttClient(emqxProperties.getBroker(),
                emqxProperties.getClientId(),
                // 设置持久化方式
                new MemoryPersistence());

        // mqtt配置
        MqttConnectOptions mqttConnectOptions = mqttConnectOptions();

        // 消息回调
        mqttClient.setCallback(this);

        // 连接
        connect(mqttConnectOptions);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("接收到 {} 消息：\n{}", topic, new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.debug("MQTT消息发布完成");
        // 发布消息完成后处理逻辑
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("MQTT客户端连接已失去", throwable);
        // 连接丢失后处理逻辑，例如重新连接
    }

    public final void publish(String topic, MqttMessage message) throws MqttException {
        mqttClient.publish(topic, message);
    }

    /**
     * @param topic
     * @param payload
     * @param qos     消息质量
     * @throws MqttException
     */
    public final void publish(String topic, byte[] payload, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(payload);
        mqttMessage.setQos(qos);
        publish(topic, mqttMessage);
    }

    public final void publish(String topic, String payload, int qos) throws MqttException {
        publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos);
    }

    // Spring 容器销毁（即关闭）时，释放资源
    @Override
    public final void destroy() throws Exception {
        if (mqttClient != null) {
            mqttClient.disconnect();
            mqttClient = null;
        }
    }

}
