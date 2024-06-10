package org.xiangqian.monolithic.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.mapper.AuthorityMapper;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xiangqian
 * @date 21:15 2024/06/02
 */
@Configuration(proxyBeanMethods = false)
public class WebConfiguration {

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Map<控制器方法，Set<角色id集合>>
     *
     * @param methodAuthoritiesMap
     * @return
     */
    @Bean
    public Map<Method, Set<Long>> methodRoleIdsMap(@Qualifier("methodAuthoritiesMap") Map<Method, List<AuthorityEntity>> methodAuthoritiesMap) {
        authorityMapper.list();

        return new HashMap<>();
    }

    /**
     * Map<控制器方法，List<权限集合>>
     *
     * @param requestMappingHandlerMapping
     * @return
     */
    @Bean
    public Map<Method, List<AuthorityEntity>> methodAuthoritiesMap(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        Map<Method, List<AuthorityEntity>> methodAuthoritiesMap = new HashMap<>(map.size(), 1f);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            String rem = "";
            HandlerMethod handlerMethod = entry.getValue();
            Tag tag = handlerMethod.getBeanType().getAnnotation(Tag.class);
            if (tag != null) {
                rem = toString(tag.name(), tag.description());
            }
            Operation operation = handlerMethod.getMethodAnnotation(Operation.class);
            if (operation != null) {
                String string = toString(operation.summary(), operation.description());
                if (StringUtils.isNotEmpty(string)) {
                    if (StringUtils.isNotEmpty(rem)) {
                        rem += " - " + string;
                    } else {
                        rem = string;
                    }
                }
            }

            byte allow = handlerMethod.getMethodAnnotation(Allow.class) == null ? 0 : (byte) 1;

            RequestMappingInfo requestMappingInfo = entry.getKey();
            RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = Optional.ofNullable(requestMethodsRequestCondition).map(RequestMethodsRequestCondition::getMethods).orElse(null);
            PathPatternsRequestCondition pathPatternsRequestCondition = requestMappingInfo.getPathPatternsCondition();
            Set<PathPattern> patterns = pathPatternsRequestCondition.getPatterns();
            List<AuthorityEntity> authorities = new ArrayList<>(patterns.size());
            for (PathPattern pattern : patterns) {
                String path = pattern.getPatternString();
                if (path.startsWith("/api/")) {
                    if (CollectionUtils.isNotEmpty(methods)) {
                        for (RequestMethod method : methods) {
                            AuthorityEntity authority = new AuthorityEntity();
                            authority.setMethod(method.name());
                            authority.setPath(path);
                            authority.setAllow(allow);
                            authority.setRem(rem);
                            authority.setDel((byte) 0);
                            authorities.add(authority);
                        }
                    } else {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setMethod("");
                        authority.setPath(path);
                        authority.setAllow(allow);
                        authority.setRem(rem);
                        authority.setDel((byte) 0);
                        authorities.add(authority);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(authorities)) {
                Method method = handlerMethod.getMethod();
                List<AuthorityEntity> alreadyExistsAuthorities = methodAuthoritiesMap.get(method);
                if (alreadyExistsAuthorities != null) {
                    alreadyExistsAuthorities.addAll(authorities);
                } else {
                    methodAuthoritiesMap.put(method, authorities);
                }
            }
        }

        // 存在多节点部署问题！（后续添加分布式锁）
        List<Long> authorityIds = new ArrayList<>(methodAuthoritiesMap.size());
        for (List<AuthorityEntity> authorities : methodAuthoritiesMap.values()) {
            for (AuthorityEntity authority : authorities) {
                AuthorityEntity queryAuthority = new AuthorityEntity();
                queryAuthority.setMethod(authority.getMethod());
                queryAuthority.setPath(authority.getPath());
                AuthorityEntity storedAuthority = authorityMapper.getOne(queryAuthority);
                if (storedAuthority == null) {
                    authorityMapper.insert(authority);
                } else {
                    authority.setId(storedAuthority.getId());
                    if (!storedAuthority.getAllow().equals(authority.getAllow())
                            || !storedAuthority.getRem().equals(authority.getRem())
                            || storedAuthority.getDel() == 1) {
                        authorityMapper.updById(authority);
                    }
                }
                authorityIds.add(authority.getId());
            }
        }
        authorityMapper.delete(new LambdaQueryWrapper<AuthorityEntity>().notIn(AuthorityEntity::getId, authorityIds));


        return methodAuthoritiesMap;
    }

    private String toString(String name, String description) {
        name = StringUtils.trimToEmpty(name);
        description = StringUtils.trimToEmpty(description);
        if (StringUtils.isEmpty(name)) {
            return description;
        }

        String string = name;
        if (StringUtils.isNotEmpty(description)) {
            string += "（" + description + "）";
        }
        return string;
    }

}
