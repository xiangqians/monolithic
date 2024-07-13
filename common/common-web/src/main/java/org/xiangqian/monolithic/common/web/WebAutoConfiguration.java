package org.xiangqian.monolithic.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * @author xiangqian
 * @date 11:19 2024/07/07
 */
@Configuration(proxyBeanMethods = false)
public class WebAutoConfiguration {

    public static <T> List<AuthorityGroupEntity> createAuthorityGroups(Set<T> tSet, Function<T, Method> handleMethodFunction, Function<T, String> pathFunction) {
        Map<String, AuthorityGroupEntity> authorityGroupMap = new HashMap<>(tSet.size(), 1f);
        for (T t : tSet) {
            // 处理方法
            Method handleMethod = handleMethodFunction.apply(t);

            // Class @RequestMapping 只映射一个路径
            Class<?> clazz = handleMethod.getDeclaringClass();
            String[] paths = Optional.ofNullable(clazz.getAnnotation(RequestMapping.class)).map(RequestMapping::value).orElse(null);
            String path = null;
            if (ArrayUtils.isEmpty(paths) || !(path = paths[0]).startsWith("/api/")) {
                continue;
            }

            // 请求方法
            RequestMethod requestMethod = null;
            if (handleMethod.isAnnotationPresent(GetMapping.class)) {
                requestMethod = RequestMethod.GET;

            } else if (handleMethod.isAnnotationPresent(PostMapping.class)) {
                requestMethod = RequestMethod.POST;

            } else if (handleMethod.isAnnotationPresent(PutMapping.class)) {
                requestMethod = RequestMethod.PUT;

            } else if (handleMethod.isAnnotationPresent(DeleteMapping.class)) {
                requestMethod = RequestMethod.DELETE;

            } else {
                continue;
            }

            // 权限组
            AuthorityGroupEntity authorityGroup = authorityGroupMap.get(path);
            if (authorityGroup == null) {
                authorityGroup = new AuthorityGroupEntity();
                authorityGroup.setPath(path);
                Tag tag = clazz.getAnnotation(Tag.class);
                if (tag != null) {
                    authorityGroup.setRem(toString(tag.name(), tag.description()));
                }
                authorityGroup.setDel((byte) 0);
                authorityGroup.setAuthorities(new ArrayList<>(8));
                authorityGroupMap.put(path, authorityGroup);
            }

            // 权限备注
            String rem = null;
            Operation operation = handleMethod.getAnnotation(Operation.class);
            if (operation != null) {
                rem = toString(operation.summary(), operation.description());
            }

            // 权限是否允许未经授权访问
            byte allow = handleMethod.getAnnotation(Allow.class) == null ? 0 : (byte) 1;

            // 请求路径
            path = pathFunction.apply(t);

            AuthorityEntity authority = new AuthorityEntity();
            authority.setMethod(requestMethod.name());
            authority.setPath(path);
            authority.setAllow(allow);
            authority.setRem(rem);
            authority.setDel((byte) 0);
            authority.setHandleMethod(handleMethod);
            List<AuthorityEntity> authorities = authorityGroup.getAuthorities();
            authorities.add(authority);
        }
        return authorityGroupMap.values().stream().toList();
    }

    private static String toString(String name, String description) {
        name = StringUtils.trim(name);
        description = StringUtils.trim(description);
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
