package org.xiangqian.monolithic.websocket;

import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.util.JsonUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 端点管理
 *
 * @author xiangqian
 * @date 19:33 2024/06/17
 */
@Component
public class EndpointManager {

    private Set<Endpoint> endpoints;

    public EndpointManager() {
        this.endpoints = new HashSet<>(16, 1f);
    }

    public synchronized void add(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    public synchronized void remove(Endpoint endpoint) {
        endpoints.remove(endpoint);
    }

    @SneakyThrows
    public void send(String topic, Object data) {
        if (CollectionUtils.isEmpty(endpoints)) {
            return;
        }

        String message = JsonUtil.serializeAsString(new Message(topic, Code.OK, data));
        for (Endpoint endpoint : endpoints) {
            Set<String> subscribedTopics = endpoint.getSubscribedTopics();
            if (CollectionUtils.isNotEmpty(subscribedTopics) && subscribedTopics.contains(topic)) {
                endpoint.send(message);
            }
        }
    }

}
