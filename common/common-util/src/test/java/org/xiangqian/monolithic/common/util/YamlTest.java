package org.xiangqian.monolithic.common.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @author xiangqian
 * @date 20:42 2023/07/31
 */
public class YamlTest {

    @Test
    public void load() {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.yml");
            Yaml yaml = new Yaml(inputStream);

            System.out.format("server: %s", yaml.getString("server")).println();
            System.out.format("server.port: %s", yaml.getInt("server.port")).println();
            System.out.format("server.servlet: %s", yaml.getString("server.servlet")).println();
            System.out.format("server.servlet.context-path: %s", yaml.getString("server.servlet.context-path")).println();
            System.out.format("spring.cloud.consul.host: %s", yaml.getString("spring.cloud.consul.host")).println();
            System.out.format("spring.cloud.consul.port: %s", yaml.getInt("spring.cloud.consul.port")).println();

            List list = yaml.getList("spring.cloud.inetutils.ignored-interfaces");
            System.out.format("spring.cloud.inetutils.ignored-interfaces: %s", list).println();

            System.out.format("spring.cloud.inetutils.ignored-interfaces[0]: %s", yaml.getString("spring.cloud.inetutils.ignored-interfaces[0]")).println();
            System.out.format("spring.cloud.inetutils.ignored-interfaces[1]: %s", yaml.getString("spring.cloud.inetutils.ignored-interfaces[1]")).println();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
