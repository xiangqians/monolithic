package org.xiangqian.monolithic.common.influxdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 21:29 2024/06/24
 */
@Configuration(proxyBeanMethods = false)
public class InfluxdbAutoConfiguration {

    @Bean
    public Influxdb influxdb(@Value("${influxdb.url}") String url, @Value("${influxdb.token}") String token, @Value("${influxdb.org}") String org, @Value("${influxdb.bucket}") String bucket) {
        return Influxdb.create(url, token, org, bucket);
    }

}
