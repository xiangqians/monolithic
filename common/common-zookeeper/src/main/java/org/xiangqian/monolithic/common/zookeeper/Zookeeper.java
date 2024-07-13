package org.xiangqian.monolithic.common.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.framework.recipes.nodes.PersistentNodeListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
@Slf4j
public class Zookeeper implements ConnectionStateListener, InitializingBean, DisposableBean {

    private ZookeeperProperties zookeeperProperties;
    private CuratorFramework curatorFramework;

    public Zookeeper(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    /**
     * Bean 所有属性被设置之后，执行一些初始化操作
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        curatorFramework = CuratorFrameworkFactory.builder()
                // 为 CuratorFramework 实例设置命名空间，所有操作都会在该命名空间下进行
                .namespace(zookeeperProperties.getNamespace())
                // 服务器连接地址，集群模式则使用逗号分隔如：host1:port1,host2:port2
                .connectString(zookeeperProperties.getConnectString())
                // 会话超时时间
                .sessionTimeoutMs((int) zookeeperProperties.getSessionTimeout().toMillis())
                // 连接超时时间
                .connectionTimeoutMs((int) zookeeperProperties.getConnectionTimeout().toMillis())
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
        curatorFramework.getConnectionStateListenable().addListener(this);
        curatorFramework.start();
    }

    /**
     * 连接状态监听器
     *
     * @param curatorFramework
     * @param connectionState
     */
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        switch (connectionState) {
            case CONNECTED:
                log.debug("Successfully connected to ZooKeeper");
                break;
            case SUSPENDED:
                log.debug("Connection to ZooKeeper suspended");
                break;
            case RECONNECTED:
                log.debug("Successfully reconnected to ZooKeeper");
                break;
            case LOST:
                log.debug("Connection to ZooKeeper lost");
                break;
            case READ_ONLY:
                log.debug("Connected to ZooKeeper in read-only mode");
                break;
        }
    }

    /**
     * 创建一个节点
     *
     * @param createMode 创建模式
     * @param path       节点路径
     * @param data       节点数据
     * @return
     * @throws Exception
     */
    private String createNode(CreateMode createMode, String path, byte[] data) throws Exception {
        return curatorFramework.create()
                // 自动创建路径中的父节点（如果它们不存在）
                .creatingParentsIfNeeded()
                // 创建模式
                .withMode(createMode)
                // 创建持久节点
                .forPath(path, data);
    }

    /**
     * 创建一个持久节点（Persistent Node）
     * 持久节点是指一旦创建，就会一直存在于ZooKeeper中，直到显式删除。持久节点通常用于存储持久化数据，如配置信息等。
     *
     * @param path 节点路径
     * @param data 节点数据
     * @return
     * @throws Exception
     */
    public String createPersistentNode(String path, byte[] data) throws Exception {
        return createNode(CreateMode.PERSISTENT, path, data);
    }

    /**
     * 创建一个持久顺序节点 (Persistent Sequential Node)
     * 持久顺序节点和持久节点类似，不同之处在于ZooKeeper会为每个节点自动分配一个单调递增的数字后缀。这些顺序号是全局唯一的，并且保证在节点创建时分配。
     *
     * @param path 节点路径
     * @param data 节点数据
     * @return
     * @throws Exception
     */
    public String createPersistentSequentialNode(String path, byte[] data) throws Exception {
        return createNode(CreateMode.PERSISTENT_SEQUENTIAL, path, data);
    }

    /**
     * 创建一个临时节点 (Ephemeral Node)
     * 临时节点是在客户端会话有效期间存在的节点。当客户端会话结束（例如客户端与ZooKeeper的连接断开），临时节点会被自动删除。临时节点通常用于表示临时状态或临时任务等。
     *
     * @param path 节点路径
     * @param data 节点数据
     * @return
     * @throws Exception
     */
    public String createEphemeralNode(String path, byte[] data) throws Exception {
        return createNode(CreateMode.EPHEMERAL, path, data);
    }

    /**
     * 创建一个临时顺序节点 (Ephemeral Sequential Node)
     * 临时顺序节点结合了临时节点和顺序节点的特性。它们在客户端会话有效期间存在，并且每个节点都会有一个唯一的顺序号。
     *
     * @param path 节点路径
     * @param data 节点数据
     * @return
     * @throws Exception
     */
    public String createEphemeralSequentialNode(String path, byte[] data) throws Exception {
        return createNode(CreateMode.EPHEMERAL_SEQUENTIAL, path, data);
    }

    /**
     * 获取节点数据
     *
     * @param path 节点路径
     * @return
     * @throws Exception
     */
    public byte[] getNodeData(String path) throws Exception {
        return curatorFramework.getData().forPath(path);
    }

    /**
     * 设置节点数据
     *
     * @param path 节点路径
     * @param data 节点数据
     * @return
     * @throws Exception
     */
    public Stat setNodeData(String path, byte[] data) throws Exception {
        return curatorFramework.setData().forPath(path, data);
    }

    /**
     * 删除节点及其子节点
     *
     * @param path 节点路径
     * @throws Exception
     */
    public void deleteNode(String path) throws Exception {
        curatorFramework.delete()
                // 递归删除指定节点及其所有子节点
                .deletingChildrenIfNeeded()
                .forPath(path);
    }

    /**
     * 检测节点是否存在
     *
     * @param path 节点路径
     * @return
     * @throws Exception
     */
    public boolean checkExists(String path) throws Exception {
        // 返回一个 Stat 对象，如果节点存在，则不为空
        Stat stat = curatorFramework.checkExists().forPath(path);
        return stat != null;
    }

    /**
     * 监听节点变化
     *
     * @param path     节点路径
     * @param listener 监听器
     * @return
     * @throws Exception
     */
    public NodeCache watchNode(String path, NodeCacheListener listener) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, path);

        // 添加监听器
        Listenable<NodeCacheListener> listenable = nodeCache.getListenable();
        listenable.addListener(listener);

        // true表示初始化时就缓存节点的数据
        nodeCache.start(true);

        return nodeCache;
    }

    /**
     * 监听持久节点变化
     * PersistentNode 允许你在 ZooKeeper 中持久化一个节点，并使用监听器来监听该节点的变化。
     *
     * @param path     节点路径
     * @param data     节点数据
     * @param listener 监听器
     * @throws Exception
     */
    public PersistentNode watchPersistentNode(String path, byte[] data, PersistentNodeListener listener) throws Exception {
        // 创建 PersistentNode
        PersistentNode persistentNode = new PersistentNode(curatorFramework,
                CreateMode.PERSISTENT,
                // 是否使用保护特性，这里设置为 false
                false,
                path,
                data);

        // 添加监听器
        Listenable<PersistentNodeListener> listenable = persistentNode.getListenable();
        listenable.addListener(listener);

        // 启动
        persistentNode.start();

        // 等待节点创建完成，超时时间
        persistentNode.waitForInitialCreate(5, TimeUnit.SECONDS);

        return persistentNode;
    }


    /**
     * 监听某个节点下子节点变化
     *
     * @param path     节点路径
     * @param listener 监听器
     * @return
     * @throws Exception
     */
    public PathChildrenCache watchChildNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);

        // 添加监听器
        Listenable<PathChildrenCacheListener> listenable = pathChildrenCache.getListenable();
        listenable.addListener(listener);

        // 启动
        pathChildrenCache.start();

        return pathChildrenCache;
    }

    /**
     * 分布式可重入互斥锁
     *
     * @param path
     * @return
     */
    public ZookeeperReentrantLock reentrantLock(String path) {
        return new ZookeeperReentrantLock(curatorFramework, path);
    }

    /**
     * 分布式不可重入互斥锁
     *
     * @param path
     * @return
     */
    public ZookeeperNonReentrantLock nonReentrantLock(String path) {
        return new ZookeeperNonReentrantLock(curatorFramework, path);
    }

    /**
     * 分布式读写锁
     *
     * @param path
     * @return
     */
    public ZookeeperReadWriteLock readWriteLock(String path) {
        return new ZookeeperReadWriteLock(curatorFramework, path);
    }

    /**
     * 多锁对象
     *
     * @param paths
     * @return
     */
    public ZookeeperMultiLock multiLock(List<String> paths) {
        return new ZookeeperMultiLock(curatorFramework, paths);
    }

    /**
     * 分布式信号量
     *
     * @param path      节点路径
     * @param maxLeases 最大租约数
     * @return
     */
    public ZookeeperSemaphore semaphore(String path, int maxLeases) {
        return new ZookeeperSemaphore(curatorFramework, path, maxLeases);
    }

    /**
     * Bean 即将被销毁时，执行一些清理操作
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        if (curatorFramework != null) {
            curatorFramework.close();
            curatorFramework = null;
        }
    }

}
