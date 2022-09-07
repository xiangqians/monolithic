package org.monolithic.configure.security;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源许可配置器
 *
 * @author xiangqian
 * @date 21:06 2022/09/07
 */
@Component
public class ResourcePermitConfigurer {

    @Getter
    private Set<PermitRequest> permitRequests;

    @Autowired
    public void setPermitRequests(List<PermitRequest> permitRequests, List<PermitRequests> permitRequestss) {
        if (CollectionUtils.isNotEmpty(permitRequestss)) {
            for (PermitRequests s : permitRequestss) {
                if (Objects.isNull(s) || CollectionUtils.isEmpty(s.get())) {
                    continue;
                }
                permitRequests.addAll(s.get());
            }
        }

        Map<HttpMethod, PermitRequest> map = new HashMap<>();
        for (PermitRequest permitRequest : permitRequests) {
            HttpMethod httpMethod = permitRequest.getHttpMethod();
            PermitRequest newPermitRequest = map.get(httpMethod);
            if (Objects.isNull(newPermitRequest)) {
                newPermitRequest = new PermitRequest(httpMethod, new HashSet<>());
                map.put(httpMethod, newPermitRequest);
            }
            newPermitRequest.getPatterns().addAll(permitRequest.getPatterns());
        }
        this.permitRequests = map.values().stream()
                .map(permitRequest -> permitRequest.getPatterns().contains("/**") ? new PermitRequest(permitRequest.getHttpMethod(), "/**") : permitRequest)
                .filter(permitRequest -> CollectionUtils.isNotEmpty(permitRequest.getPatterns()))
                .collect(Collectors.toSet());
    }

    public void configure(HttpSecurity http) throws Exception {
        if (CollectionUtils.isEmpty(permitRequests)) {
            return;
        }

        for (PermitRequest permitRequest : permitRequests) {
            HttpMethod httpMethod = permitRequest.getHttpMethod();
            Set<String> patterns = permitRequest.getPatterns();
            if (httpMethod != null) {
                http.authorizeRequests().antMatchers(httpMethod, patterns.toArray(String[]::new)).permitAll();
            } else {
                http.authorizeRequests().antMatchers(patterns.toArray(String[]::new)).permitAll();
            }
        }
    }

}
