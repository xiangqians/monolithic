package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 22:26 2024/02/28
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ZookeeperProperties.class})
public class ZookeeperAutoConfiguration {

    @Bean
    public Zookeeper zookeeper(ZookeeperProperties zookeeperProperties) {
        return new Zookeeper(  zookeeperProperties);
    }

}
