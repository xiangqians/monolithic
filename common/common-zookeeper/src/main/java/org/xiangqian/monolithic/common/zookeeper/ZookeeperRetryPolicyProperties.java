package org.xiangqian.monolithic.common.zookeeper;

import lombok.Data;

import java.time.Duration;

/**
 * Zookeeper 重试策略配置
 *
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
@Data
public class ZookeeperRetryPolicyProperties {

    /**
     * 初始化间隔时间，时间格式：{n}h{n}m{n}s
     */
    private Duration baseSleepTime;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 最大重试间隔时间，时间格式：{n}h{n}m{n}s
     */
    private Duration maxSleep;

}
