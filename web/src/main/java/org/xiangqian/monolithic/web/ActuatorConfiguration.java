package org.xiangqian.monolithic.web;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Actuator配置
 * <p>
 * Spring Boot Actuator 是 Spring Boot 提供的用于监控和管理应用程序的模块
 *
 * @author xiangqian
 * @date 23:58 2024/06/04
 */
@Configuration(proxyBeanMethods = false)
public class ActuatorConfiguration {

    @Value("${spring.boot.admin.secret}")
    private String secret;

    /**
     * {@link de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration.BlockingRegistrationClientConfig#registrationClient(de.codecentric.boot.admin.client.config.ClientProperties)}
     * {@link de.codecentric.boot.admin.client.registration.RegistrationClient}
     * {@link de.codecentric.boot.admin.client.registration.BlockingRegistrationClient}
     * {@link de.codecentric.boot.admin.client.registration.BlockingRegistrationClient#register(java.lang.String, de.codecentric.boot.admin.client.registration.Application)}
     *
     * @param client
     * @return
     */
    @Bean
    public RegistrationClient registrationClient(ClientProperties client) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder(new RestTemplateCustomizer[0])
                .setConnectTimeout(client.getConnectTimeout())
                .setReadTimeout(client.getReadTimeout());
        RestTemplate restTemplate = restTemplateBuilder.build();
        return new BlockingRegistrationClient(restTemplate) {
            private HttpHeaders headers;

            {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.set("Secret", secret);
            }

            @Override
            protected HttpHeaders createRequestHeaders() {
                return headers;
            }
        };
    }

    /**
     * HTTP Exchanges
     * 开启 http trace 记录
     * <p>
     * https://docs.spring.io/spring-boot/docs/3.1.0/reference/html/actuator.html#actuator.http-exchanges
     * 从官方文档中可以看出，不建议在生产环境下使用。
     * 在生产环境下，推荐使用第三方组件实现对 HttpTraces 的监控，比如 Zipkin or Spring Cloud Sleuth, Prometheus+Grafana，或者自己实现 HttpExchangeRepository
     *
     * @return
     */
    @Bean
    @Profile({"dev", "test"}) // 仅在 dev、test 环境下开启 HTTP Exchanges
    public HttpExchangeRepository httpTraceRepository() {
        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        // 默认保存1000条http请求记录
        repository.setCapacity(1000);
        return repository;
    }

}
