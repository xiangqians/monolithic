package org.xiangqian.monolithic.webmvc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityGroupMapper;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityMapper;
import org.xiangqian.monolithic.common.redis.Redis;
import org.xiangqian.monolithic.common.redis.RedisHash;
import org.xiangqian.monolithic.common.redis.RedisLock;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.util.Md5Util;
import org.xiangqian.monolithic.common.web.Allow;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 方法安全
 *
 * @author xiangqian
 * @date 16:13 2024/06/02
 */
@Component
public class MethodSecurity implements ApplicationRunner {

    private final String ALLOWS = "web_allows";

    @Autowired
    private Redis redis;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private AuthorityGroupMapper authorityGroupMapper;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 是否允许未经授权访问
     *
     * @param method 请求方法
     * @param path   请求路径
     * @return
     */
    public boolean isAllow(String method, String path) {
        return redis.Set(ALLOWS).isMember(method + path);
    }

    public boolean hasRoleId(Method handleMethod, String method, Long roleId) {
        return redis.Hash(getHandleMethodKey(handleMethod)).hasKey(method + "_" + roleId);
    }

    @SneakyThrows
    public AuthorityEntity getAuthority(Method handleMethod, String method) {
        Object value = redis.Hash(getHandleMethodKey(handleMethod)).get(method);
        if (value == null) {
            return null;
        }
        return JsonUtil.deserialize(value.toString(), AuthorityEntity.class);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedisLock redisLock = redis.Lock("web_method_authority_lock");
        try {
            if (redisLock.tryLock()) {
                run();
            }
        } finally {
            redisLock.forceUnlock();
        }
    }

    private void run() {
        List<AuthorityGroupEntity> authorityGroups = getAuthorityGroups();
        db(authorityGroups);
        role(authorityGroups);
        Map<Method, List<AuthorityEntity>> handleMethodAuthoritiesMap = getHandleMethodAuthoritiesMap(authorityGroups);
        set(handleMethodAuthoritiesMap);
    }

    @SneakyThrows
    private void set(Map<Method, List<AuthorityEntity>> handleMethodAuthoritiesMap) {
        // 允许未经授权访问集合
        Set<String> allows = new HashSet<>(8, 1f);

        int count = 3;
        while (count-- > 0) {
            Set<String> keys = redis.getKeysWithPrefix("web_method_", 10000);
            if (CollectionUtils.isEmpty(keys)) {
                break;
            }
            keys.forEach(redis::delete);
        }

        for (Map.Entry<Method, List<AuthorityEntity>> entry : handleMethodAuthoritiesMap.entrySet()) {
            Method handleMethod = entry.getKey();
            String key = getHandleMethodKey(handleMethod);
            RedisHash redisHash = redis.Hash(key);
            List<AuthorityEntity> authorities = entry.getValue();
            for (AuthorityEntity authority : authorities) {
                Long id = authority.getId();
                String method = authority.getMethod();
                String path = authority.getPath();
                Byte allow = authority.getAllow();
                redisHash.put(method, JsonUtil.serializeAsString(Map.of("id", id,
                        "method", method,
                        "path", path,
                        "allow", allow)));

                Set<Long> roleIds = authority.getRoleIds();
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    for (Long roleId : roleIds) {
                        redisHash.put(method + "_" + roleId, null);
                    }
                }

                // 是否允许未经授权访问
                if (Byte.valueOf((byte) 1).equals(allow)) {
                    allows.add(method + path);
                }
            }
        }

        redis.delete(ALLOWS);
        redis.Set(ALLOWS).add(allows.toArray(String[]::new));
    }

    private String getHandleMethodKey(Method handleMethod) {
        String md5 = Md5Util.encryptToHexString(String.format("%s.%s(%s)",
                handleMethod.getDeclaringClass().getTypeName(),
                handleMethod.getName(),
                StringUtils.join(Arrays.asList(handleMethod.getParameterTypes()).stream().map(Type::getTypeName).collect(Collectors.toList()), ",")));
        return "web_method_" + md5;
    }

    /**
     * Map<处理方法，List<权限集合>>
     *
     * @param authorityGroups
     */
    private Map<Method, List<AuthorityEntity>> getHandleMethodAuthoritiesMap(List<AuthorityGroupEntity> authorityGroups) {
        Map<Method, List<AuthorityEntity>> handleMethodAuthoritiesMap = new HashMap<>(authorityGroups.size(), 1f);
        for (AuthorityGroupEntity authorityGroup : authorityGroups) {
            for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                Method handleMethod = authority.getHandleMethod();
                List<AuthorityEntity> authorities = handleMethodAuthoritiesMap.get(handleMethod);
                if (authorities == null) {
                    authorities = new ArrayList<>(8);
                    handleMethodAuthoritiesMap.put(handleMethod, authorities);
                }
                authorities.add(authority);
            }
        }
        return handleMethodAuthoritiesMap;
    }

    private void role(List<AuthorityGroupEntity> authorityGroups) {
        // 获取权限和角色关系
        Map<Long, AuthorityEntity> authorityMap = authorityMapper.list().stream().collect(Collectors.toMap(AuthorityEntity::getId, Function.identity()));
        for (AuthorityGroupEntity authorityGroup : authorityGroups) {
            for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                AuthorityEntity value = authorityMap.get(authority.getId());
                if (value != null) {
                    authority.setRoleIds(value.getRoleIds());
                }
            }
        }
    }

    private void db(List<AuthorityGroupEntity> authorityGroups) {
        int size = authorityGroups.size();
        List<Long> authorityGroupIds = new ArrayList<>(size);
        List<Long> authorityIds = new ArrayList<>(size);
        for (AuthorityGroupEntity authorityGroup : authorityGroups) {
            AuthorityGroupEntity queryAuthorityGroup = new AuthorityGroupEntity();
            queryAuthorityGroup.setPath(authorityGroup.getPath());
            AuthorityGroupEntity storedAuthorityGroup = authorityGroupMapper.getOne(queryAuthorityGroup);
            if (storedAuthorityGroup == null) {
                authorityGroupMapper.insert(authorityGroup);
            } else {
                authorityGroup.setId(storedAuthorityGroup.getId());
                if (!storedAuthorityGroup.getRem().equals(authorityGroup.getRem()) || storedAuthorityGroup.getDel() == 1) {
                    authorityGroupMapper.updById(authorityGroup);
                }
            }
            Long authorityGroupId = authorityGroup.getId();
            authorityGroupIds.add(authorityGroupId);

            for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                authority.setGroupId(authorityGroupId);

                AuthorityEntity queryAuthority = new AuthorityEntity();
                queryAuthority.setMethod(authority.getMethod());
                queryAuthority.setPath(authority.getPath());
                AuthorityEntity storedAuthority = authorityMapper.getOne(queryAuthority);
                if (storedAuthority == null) {
                    authorityMapper.insert(authority);
                } else {
                    authority.setId(storedAuthority.getId());
                    if (!storedAuthority.getGroupId().equals(authority.getGroupId())
                            || !storedAuthority.getAllow().equals(authority.getAllow())
                            || !storedAuthority.getRem().equals(authority.getRem())
                            || storedAuthority.getDel() == 1) {
                        authorityMapper.updById(authority);
                    }
                }
                authorityIds.add(authority.getId());
            }
        }
        authorityGroupMapper.delete(new LambdaQueryWrapper<AuthorityGroupEntity>().notIn(AuthorityGroupEntity::getId, authorityGroupIds));
        authorityMapper.delete(new LambdaQueryWrapper<AuthorityEntity>().notIn(AuthorityEntity::getId, authorityIds));
    }

    private List<AuthorityGroupEntity> getAuthorityGroups() {
        Map<RequestMappingInfo, HandlerMethod> handleMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<String, AuthorityGroupEntity> authorityGroupMap = new HashMap<>(handleMethodMap.size(), 1f);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handleMethodMap.entrySet()) {
            Method handleMethod = entry.getValue().getMethod();

            // Class @RequestMapping 只映射一个路径
            Class<?> clazz = handleMethod.getDeclaringClass();
            String[] paths = Optional.ofNullable(clazz.getAnnotation(RequestMapping.class)).map(RequestMapping::value).orElse(null);
            String path = null;
            if (ArrayUtils.isEmpty(paths) || !(path = paths[0]).startsWith("/api/")) {
                continue;
            }

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

            String rem = null;
            Operation operation = handleMethod.getAnnotation(Operation.class);
            if (operation != null) {
                rem = toString(operation.summary(), operation.description());
            }

            byte allow = handleMethod.getAnnotation(Allow.class) == null ? 0 : (byte) 1;

            RequestMappingInfo requestMappingInfo = entry.getKey();
            RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = Optional.ofNullable(requestMethodsRequestCondition).map(RequestMethodsRequestCondition::getMethods).orElse(null);
            if (CollectionUtils.isEmpty(methods)) {
                methods = Set.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE);
            }
            PathPatternsRequestCondition pathPatternsRequestCondition = requestMappingInfo.getPathPatternsCondition();
            Set<PathPattern> patterns = pathPatternsRequestCondition.getPatterns();
            // Method @RequestMapping 只映射一个路径
            PathPattern pattern = patterns.iterator().next();
            path = pattern.getPatternString();
            List<AuthorityEntity> authorities = authorityGroup.getAuthorities();
            for (RequestMethod method : methods) {
                AuthorityEntity authority = new AuthorityEntity();
                authority.setMethod(method.name());
                authority.setPath(path);
                authority.setAllow(allow);
                authority.setRem(rem);
                authority.setDel((byte) 0);
                authority.setHandleMethod(handleMethod);
                authorities.add(authority);
            }
        }

        return authorityGroupMap.values().stream().toList();
    }

    private String toString(String name, String description) {
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
