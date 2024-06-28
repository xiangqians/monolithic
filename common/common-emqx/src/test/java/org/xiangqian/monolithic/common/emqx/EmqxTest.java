package org.xiangqian.monolithic.common.emqx;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:33 2024/06/28
 */
public class EmqxTest {

    private Emqx emqx;

    @Before
    @SneakyThrows
    public void before() {
        EmqxProperties emqxProperties = new EmqxProperties();
        emqxProperties.setBroker("tcp://localhost:1883");
        emqxProperties.setClientId("publisher");
        emqxProperties.setUser("user");
        emqxProperties.setPasswd("passwd");
        emqx = new Emqx(emqxProperties);
        emqx.run(null);
    }

    @After
    public void after() throws Exception {
        emqx.destroy();
    }

    @Test
    @SneakyThrows
    public void publisher() {
        emqx.publish("sys", LocalDateTime.now().toString(), 0);
        TimeUnit.SECONDS.sleep(2);
    }

}
