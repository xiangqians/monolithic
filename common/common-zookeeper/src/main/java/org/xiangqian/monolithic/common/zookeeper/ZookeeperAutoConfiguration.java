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
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                // 为 CuratorFramework 实例设置命名空间，所有操作都会在该命名空间下进行
                .namespace(zookeeperProperties.getNamespace())
                // 服务器连接地址，集群模式则使用逗号分隔如：host1:port1,host2:port2
                .connectString(zookeeperProperties.getConnectString())
                // 会话超时时间
                .sessionTimeoutMs((int) zookeeperProperties.getSessionTimeout().toMillis())
                // 连接超时时间
                .connectionTimeoutMs((int) zookeeperProperties.getConnectTimeout().toMillis())
                // 设置访问授权信息，可以在连接ZooKeeper时进行身份验证
//                .authorization("digest", "username:password".getBytes())
                // 设置是否允许在连接到ZooKeeper集群的时候进入只读模式
                .canBeReadOnly(false)
                // 设置数据压缩提供者，用于对数据进行压缩
                .compressionProvider(new GzipCompressionProvider())
                // 设置用于创建Curator使用的线程的工厂
//                .threadFactory()
                // 重试策略，在与ZooKeeper服务器交互时发生错误后的重试行为
                .retryPolicy(new ExponentialBackoffRetry((int) zookeeperProperties.getRetryPolicy().getBaseSleepTime().toMillis(),
                        zookeeperProperties.getRetryPolicy().getMaxRetries(),
                        (int) zookeeperProperties.getRetryPolicy().getMaxSleep().toMillis()))
                .build();
        curatorFramework.start();
        return new Zookeeper(curatorFramework);
    }

}
