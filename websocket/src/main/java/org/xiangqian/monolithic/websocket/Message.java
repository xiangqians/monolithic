package org.xiangqian.monolithic.websocket;

import lombok.Data;
import org.xiangqian.monolithic.common.util.Response;

/**
 * @author xiangqian
 * @date 21:39 2024/06/17
 */
@Data
public class Message extends Response<Object> {
    // 主题
    private String topic;

    public Message(String topic, String code) {
        super(code);
        this.topic = topic;
    }

    public Message(String topic, String code, Object data) {
        super(code, data);
        this.topic = topic;
    }

    public Message(String topic, String code, String msg, Object data) {
        super(code, msg, data);
        this.topic = topic;
    }

}
