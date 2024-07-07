package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
public class Zookeeper implements DisposableBean {

    private CuratorFramework curatorFramework;

    public Zookeeper(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public ZookeeperLock Lock(String path) {
        return new ZookeeperLock(curatorFramework, path);
    }

    // Spring 容器销毁（即关闭）时，释放资源
    @Override
    public void destroy() throws Exception {
        if (curatorFramework != null) {
            curatorFramework.close();
            curatorFramework = null;
        }
    }

}
