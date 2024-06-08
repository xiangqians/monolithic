package org.xiangqian.monolithic.web;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author xiangqian
 * @date 23:58 2024/06/04
 */
@Configuration(proxyBeanMethods = false)
public class ActuatorConfiguration {

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
