package org.xiangqian.monolithic.common.influxdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:42 2024/06/24
 */
public class InfluxdbTest {

    private Influxdb influxdb;

    @Before
    public void before() {
        influxdb = Influxdb.create("http://localhost:8086", "4zKtAjyDercFgMpHBCndHzhMrBx-q3G400Mvnudm98mZ1sBuJONalemJWXywXEu-KgXmerDBWOAFp4JF3rCcSQ==", "test", "test");
    }

    @After
    public void after() throws IOException {
        influxdb.close();
    }

    @Test
    public void writePoint() {
        influxdb.writePoint("temperature", Map.of("location", "office"), Map.of("value", 23.5), Duration.ofSeconds(2));
    }

}
