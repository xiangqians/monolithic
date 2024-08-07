package org.xiangqian.monolithic.common.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Zookeeper 配置
 *
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
@Data
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    /**
     * 设置实例命名空间，所有操作都会在该命名空间下进行
     */
    private String namespace;

    /**
     * 服务器连接地址，集群模式则使用逗号分隔，如：host1:port1,host2:port2
     */
    private String connectString;

    /**
     * 会话超时时间
     */
    private Duration sessionTimeout;

    /**
     * 连接超时时间
     */
    private Duration connectionTimeout;

    /**
     * Zookeeper 重试策略配置
     */
    private ZookeeperRetryPolicyProperties retryPolicy;

}
