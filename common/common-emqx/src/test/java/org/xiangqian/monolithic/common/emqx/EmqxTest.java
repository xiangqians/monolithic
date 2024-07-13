package org.xiangqian.monolithic.common.emqx;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.Yaml;

import java.io.InputStream;

/**
 * @author xiangqian
 * @date 21:33 2024/06/28
 */
public class EmqxTest {

    private Emqx emqx;

    @Data
    public static class ApplicationProperties {
        private EmqxProperties emqx;
    }

    @Before
    @SneakyThrows
    public void before() {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml");
            ApplicationProperties applicationProperties = Yaml.loadAs(inputStream, ApplicationProperties.class);
            EmqxProperties emqxProperties = applicationProperties.getEmqx();
            emqx = new Emqx(emqxProperties);
            emqx.run(null);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @After
    public void after() throws Exception {
        emqx.destroy();
    }

    @Test
    @SneakyThrows
    public void testPublish() {

        System.out.println();

//        emqx.publish("sys", LocalDateTime.now().toString(), 0);
//        TimeUnit.SECONDS.sleep(2);
    }

}
