package org.xiangqian.monolithic.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.web.WebAutoConfiguration;
import org.xiangqian.monolithic.common.web.WebDocConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiangqian
 * @date 11:21 2024/07/07
 */
@Import(WebDocConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class WebfluxConfiguration {

    @Bean
    public List<AuthorityGroupEntity> authorityGroups(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        // 请求方法映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();

        return WebAutoConfiguration.createAuthorityGroups(handlerMethodMap.entrySet(),
                entry -> entry.getValue().getMethod(),
                entry -> {
                    RequestMappingInfo requestMappingInfo = entry.getKey();
                    PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
                    Set<PathPattern> pathPatterns = patternsRequestCondition.getPatterns();
                    // Method @RequestMapping 只映射一个路径
                    PathPattern pathPattern = pathPatterns.iterator().next();
                    return pathPattern.getPatternString();
                });
    }

}
