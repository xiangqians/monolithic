package org.xiangqian.monolithic.common.zookeeper;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.Yaml;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 20:24 2024/07/12
 */
public class ZookeeperTest {

    protected Zookeeper zookeeper;

    @Data
    public static class ApplicationProperties {
        private ZookeeperProperties zookeeper;
    }

    @Before
    @SneakyThrows
    public void before() {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml");
            ApplicationProperties applicationProperties = Yaml.loadAs(inputStream, ApplicationProperties.class);
            ZookeeperProperties zookeeperProperties = applicationProperties.getZookeeper();
            System.out.println(zookeeperProperties);
            zookeeper = new Zookeeper(zookeeperProperties);
            zookeeper.afterPropertiesSet();
            System.out.println("start");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @After
    public void after() throws Exception {
        System.out.println("destroy");
        zookeeper.destroy();
    }

    @Test
    @SneakyThrows
    public void testCreateNode() {
        String result = zookeeper.createPersistentNode("/test", DateTimeUtil.format(LocalDateTime.now()).getBytes(CharEncoding.UTF_8));
        System.out.println(result);
    }

    @Test
    @SneakyThrows
    public void testGetNodeData() {
        byte[] data = zookeeper.getNodeData("/test");
        System.out.println(new String(data, CharEncoding.UTF_8));
    }

    @Test
    @SneakyThrows
    public void testSetNodeData() {
        Stat stat = zookeeper.setNodeData("/test", DateTimeUtil.format(LocalDateTime.now()).getBytes(CharEncoding.UTF_8));
        System.out.println(stat);
    }

    @Test
    @SneakyThrows
    public void testDeleteNode() {
        zookeeper.deleteNode("/test");
    }

    @Test
    @SneakyThrows
    public void testCheckExists() {
        boolean exists = zookeeper.checkExists("/test");
        System.out.println(exists);
    }

    private NodeCache nodeCache;

    @Test
    @SneakyThrows
    public void testWatchNode() {
        nodeCache = zookeeper.watchNode("/test", () -> System.out.format("节点发生了变化：%s", new String(nodeCache.getCurrentData().getData(), CharEncoding.UTF_8)).println());
        TimeUnit.MINUTES.sleep(5);

        IOUtils.closeQuietly(nodeCache);
        System.out.println("关闭监听器");
    }

    @Test
    @SneakyThrows
    public void testWatchPersistentNode() {
        PersistentNode persistentNode = zookeeper.watchPersistentNode("/test", DateTimeUtil.format(LocalDateTime.now()).getBytes(CharEncoding.UTF_8), path -> System.out.println(path));
        TimeUnit.MINUTES.sleep(5);

        IOUtils.closeQuietly(persistentNode);
        System.out.println("关闭监听器");
    }

    @Test
    @SneakyThrows
    public void testWatchChildNode() {
        PathChildrenCache pathChildrenCache = zookeeper.watchChildNode("/test", (curatorFramework, pathChildrenCacheEvent) -> {
            ChildData childData = pathChildrenCacheEvent.getData();
            String path = childData.getPath();
            byte[] data = childData.getData();

            // 处理子节点事件
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.format("[Child added] %s, %s", path, new String(data, CharEncoding.UTF_8)).println();
                    break;
                case CHILD_UPDATED:
                    System.out.format("[Child updated] %s, %s", path, new String(data, CharEncoding.UTF_8)).println();
                    break;
                case CHILD_REMOVED:
                    System.out.format("[Child removed] %s, %s", path, new String(data, CharEncoding.UTF_8)).println();
                    break;
                default:
                    System.err.println(pathChildrenCacheEvent.getData().getPath());
                    break;
            }
        });
        TimeUnit.MINUTES.sleep(5);

        IOUtils.closeQuietly(pathChildrenCache);
        System.out.println("关闭监听器");
    }

}
