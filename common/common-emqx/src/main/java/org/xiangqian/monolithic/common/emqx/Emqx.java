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

    private EmqxProperties emqxProperties;
    private MqttClient mqttClient;

    public Emqx(EmqxProperties emqxProperties) {
        this.emqxProperties = emqxProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 创建 MQTT 客户端
        mqttClient = new MqttClient(emqxProperties.getUrl(),
                // 客户端 ID
                emqxProperties.getClientId(),
                // 设置持久化方式
                new MemoryPersistence());

        // MQTT 配置
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        // 配置 MQTT 客户端连接到代理时的会话状态
        // 1、true：表示创建一个“干净会话”（clean session）。这意味着客户端连接到代理后，代理不会保留任何关于客户端的状态信息。当客户端断开连接时，代理会立即删除该客户端的订阅信息和其他会话状态信息。下次客户端连接时，将从头开始建立新的会话。
        // 2、false：表示创建一个持久化会话。这时，客户端连接到代理后，代理会保留客户端的订阅信息和其他会话状态信息。即使客户端断开连接，代理也会保留这些信息。下次客户端连接时，可以恢复之前的订阅状态和其他会话信息。
        //
        // 在实际应用中，选择是否使用持久化会话或干净会话通常取决于应用的需求和设计考虑：
        // 1、干净会话 (cleanSession(true)) 的使用场景：
        //   临时性连接，不需要保留订阅状态和其他会话信息。
        //   对消息传递的即时性要求较高，不需要代理缓存消息等待客户端连接。
        // 2、持久化会话 (cleanSession(false)) 的使用场景：
        //   长期运行的客户端，需要保持订阅状态，确保不会错过消息。
        //   对消息传递的可靠性要求较高，即使客户端断开连接，也能确保消息能够传递到客户端。
        mqttConnectOptions.setCleanSession(emqxProperties.getCleanSession());

        // 认证用户
        mqttConnectOptions.setUserName(emqxProperties.getUsername());
        // 认证密码
        mqttConnectOptions.setPassword(emqxProperties.getPassword().toCharArray());

        // 设置连接超时时间，单位：s
        mqttConnectOptions.setConnectionTimeout((int) emqxProperties.getConnectionTimeout().toSeconds());

        // 设置心跳间隔，单位：s
        mqttConnectOptions.setKeepAliveInterval((int) emqxProperties.getKeepAliveInterval().toSeconds());

        // 设置是否自动重连
        mqttConnectOptions.setAutomaticReconnect(true);

        // 消息回调
        mqttClient.setCallback(this);

        // 连接之前
        connectBefore(mqttClient, mqttConnectOptions);

        // 连接
        mqttClient.connect(mqttConnectOptions);
        log.debug("MQTT已连接！");

        // 连接之后
        connectAfter(mqttClient);
    }

    /**
     * 连接之前
     *
     * @param mqttClient
     * @param mqttConnectOptions
     */
    protected void connectBefore(MqttClient mqttClient, MqttConnectOptions mqttConnectOptions) {
    }

    /**
     * 连接之后
     *
     * @param mqttClient
     */
    protected void connectAfter(MqttClient mqttClient) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("接收到主题 {} 消息 {}", topic, toString(mqttMessage));
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

    public static String toString(MqttMessage mqttMessage) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n\t").append("Message ID: ").append(mqttMessage.getId());

        // 检查消息是否为重复消息
        builder.append("\n\t").append("Duplicate : ").append(mqttMessage.isDuplicate());

        // 检查消息是否为保留消息
        builder.append("\n\t").append("Retained  : ").append(mqttMessage.isRetained());

        builder.append("\n\t").append("QoS\t\t  : ").append(mqttMessage.getQos());
        builder.append("\n\t").append("Payload   : ").append(new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));

        return builder.toString();
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param payload 消息内容
     * @param qos     消息质量服务等级
     * @throws MqttException
     */
    public void publish(String topic, byte[] payload, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(payload);
        mqttMessage.setQos(qos);
        mqttClient.publish(topic, mqttMessage);
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param payload 消息内容
     * @param qos     消息质量服务等级
     * @throws MqttException
     */
    public void publish(String topic, String payload, int qos) throws MqttException {
        publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos);
    }

    // Spring 容器销毁（即关闭）时，释放资源
    @Override
    public void destroy() throws Exception {
        if (mqttClient != null) {
            mqttClient.disconnect();
            mqttClient = null;
        }
    }

}
