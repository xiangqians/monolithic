package org.xiangqian.monolithic.common.emqx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 21:57 2024/06/28
 */
@Data
@ConfigurationProperties(prefix = "emqx")
public class EmqxProperties {

    /**
     * 服务器地址和端口
     */
    private String url;

    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 是否清除会话
     */
    private Boolean cleanSession;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接超时时间
     */
    private Duration connectionTimeout;

    /**
     * 心跳包发送间隔时间
     */
    private Duration keepAliveInterval;

}
