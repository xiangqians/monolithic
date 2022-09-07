package org.monolithic.configure.security;

import com.google.common.collect.Sets;
import org.monolithic.annotation.AllowUARequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * 资源许可配置
 *
 * @author xiangqian
 * @date 21:37 2022/09/07
 */
@Configuration
public class ResourcePermitConfiguration {

    // security
    @Bean
    public PermitRequest securityResourcePermitConfigure() {
        return new PermitRequest("/user/login");
    }

    // 静态资源许可配置
    @Bean
    public PermitRequest staticResourcePermitConfigure() {
        return new PermitRequest("/img/**", "/video/**", "/static/**");
    }

    // document资源许可配置
    @Bean
    public PermitRequest documentResourcePermitConfigure() {
        return new PermitRequest("/swagger-resources/**",
                "/webjars/**",
                "/v2/**",
                "/swagger-ui.html/**",
                "/api-docs",
                "/api-docs/**",
                "/doc.html/**");
    }

    @Bean
    public PermitRequests allowUARequestConfigure(WebApplicationContext applicationContext) {
        List<PermitRequest> permitRequests = new ArrayList<>();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            AllowUARequest allowUARequest = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), AllowUARequest.class);
            if (Objects.isNull(allowUARequest)) {
                continue;
            }

            RequestMappingInfo requestMappingInfo = entry.getKey();
            HttpMethod httpMethod = Optional.ofNullable(requestMappingInfo.getMethodsCondition().getMethods())
                    .map(methods -> HttpMethod.resolve(methods.iterator().next().name()))
                    .orElse(null);
            Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
            for (String pattern : patterns) {
                PermitRequest permitRequest = new PermitRequest(httpMethod, Sets.newHashSet(pattern.replaceAll("\\{(.*?)\\}", "*")));
                permitRequests.add(permitRequest);
            }
        }

        return new PermitRequests(permitRequests);
    }

}
