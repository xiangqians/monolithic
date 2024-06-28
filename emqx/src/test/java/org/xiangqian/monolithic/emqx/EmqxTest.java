package org.xiangqian.monolithic.emqx;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.emqx.EmqxProperties;

import java.util.concurrent.CountDownLatch;

/**
 * @author xiangqian
 * @date 21:33 2024/06/28
 */
public class EmqxTest {

    private DefaultEmqx emqx;

    @Before
    @SneakyThrows
    public void before() {
        EmqxProperties emqxProperties = new EmqxProperties();
        emqxProperties.setBroker("tcp://localhost:1883");
        emqxProperties.setClientId("subscriber");
        emqxProperties.setUser("user");
        emqxProperties.setPasswd("passwd");
        emqx = new DefaultEmqx(emqxProperties);
        emqx.run(null);
    }

    @After
    @SneakyThrows
    public void after() {
        emqx.destroy();
    }

    @Test
    @SneakyThrows
    public void subscribe() {
        System.out.println(emqx);
        new CountDownLatch(1).await();
    }

}
