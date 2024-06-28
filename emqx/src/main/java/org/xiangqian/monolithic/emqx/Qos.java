package org.xiangqian.monolithic.emqx;

/**
 * 在 MQTT（Message Queuing Telemetry Transport）协议中，QoS（Quality of Service，服务质量）是用来确保消息传递的可靠性和交付顺序的级别。
 * QoS级别定义了消息发布者和订阅者之间达成的协议，确保消息能够按照特定的级别进行传输。
 *
 * @author xiangqian
 * @date 23:13 2024/06/28
 */
public interface Qos {

    /**
     * 最多一次（QoS 0，At Most Once）
     * 消息发布者发送消息后，不会收到任何确认，消息可能会丢失或多次传递。
     */
    int AT_MOST_ONCE = 0;

    /**
     * 至少一次（QoS 1，At Least Once）
     * 消息发布者会收到消息传递的确认（PUBACK），如果确认超时或丢失，消息可能会重复传递，但不会丢失。
     */
    int AT_LEAST_ONCE = 1;

    /**
     * 只有一次（QoS 2，Exactly Once）
     * 最高级别的 QoS，确保消息只传递一次，消息发布者和订阅者会进行握手确认，以确保消息的确切一次传递。
     */
    int EXACTLY_ONCE = 2;

}
