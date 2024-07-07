package org.xiangqian.monolithic.webflux;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.xiangqian.monolithic.common.web.WebDocConfiguration;

/**
 * @author xiangqian
 * @date 11:21 2024/07/07
 */
@Import(WebDocConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class WebfluxAutoConfiguration {
}
