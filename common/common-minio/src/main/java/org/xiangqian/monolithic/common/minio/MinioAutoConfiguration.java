package org.xiangqian.monolithic.common.minio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 19:13 2024/06/14
 */
@Configuration(proxyBeanMethods = false)
public class MinioAutoConfiguration {

    @Bean
    public Minio minio(@Value("${minio.endpoint}") String endpoint,
                       @Value("${minio.access-key}") String accessKey,
                       @Value("${minio.secret-key}") String secretKey) {
        return new Minio(endpoint, accessKey, secretKey);
    }

}
