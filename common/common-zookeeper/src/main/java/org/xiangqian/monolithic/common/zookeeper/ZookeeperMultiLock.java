package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;

import java.util.List;

/**
 * 多锁对象
 * 将多个锁视为单个实体进行处理。尝试获取所有锁，如果成功获取所有锁，则持有所有锁；如果其中一个获取失败，则释放已获取的所有锁。
 *
 * @author xiangqian
 * @date 21:52 2024/07/12
 */
public class ZookeeperMultiLock extends ZookeeperLock {

    ZookeeperMultiLock(InterProcessMultiLock interProcessMultiLock) {
        super(interProcessMultiLock);
    }

    ZookeeperMultiLock(CuratorFramework curatorFramework, List<String> paths) {
        this(new InterProcessMultiLock(curatorFramework, paths));
    }

}
