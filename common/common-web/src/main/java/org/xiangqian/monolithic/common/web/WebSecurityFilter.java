package org.xiangqian.monolithic.common.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.xiangqian.monolithic.common.biz.sys.service.AuthorityService;
import org.xiangqian.monolithic.common.biz.sys.service.RoleAuthorityService;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityGroupMapper;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityMapper;
import org.xiangqian.monolithic.common.util.Md5Util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 12:11 2024/07/07
 */
@Slf4j
public abstract class WebSecurityFilter<T> implements Ordered, ApplicationRunner {

    @Value("${springdoc.api-docs.enabled}")
    private Boolean enabled;

    @Value("${management.token}")
    private String token;

    @Autowired
    private UserService userService;

    /**
     * Map<{@link AuthorityEntity#getHandleMethod()}, {@link AuthorityEntity}>
     */
    private Map<Object, AuthorityEntity> authorityMap;

    @Autowired
    private List<AuthorityGroupEntity> authorityGroups;

    @Autowired
    private AuthorityGroupMapper authorityGroupMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    /**
     * 请求过滤
     *
     * @param t 请求信息
     * @return 是否放行
     */
    public boolean filter(T t) {
        // 获取请求路径
        String path = getPath(t);
        log.debug("path {}", path);

        // 接口文档请求；如果开启了接口文档，则放行
        if (BooleanUtils.isTrue(enabled)
                && (path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.equals("/doc.html")
                || path.startsWith("/webjars"))) {
            return true;
        }

        // 监控应用程序请求：/actuator/*
        if (path.startsWith("/actuator")) {
            String token = getToken(t);
            if (StringUtils.isNotEmpty(token) && token.equals(this.token)) {
                return true;
            }
            return false;
        }

        // 非 /api/ 请求前缀
        if (!path.startsWith("/api/")) {
            return false;
        }

        // 是否允许未经授权访问
        if (authorityService.isAllow(getMethod(t), path)) {
            return true;
        }

        // 获取token
        String token = getToken(t);
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        // 根据token获取用户信息
        UserEntity user = userService.getByToken(token);
        if (user == null) {
            return false;
        }

        // 设置登录用户信息
        setUser(t, user);

        // 是否是系统管理员角色
        if (user.isAdminRole()) {
            return true;
        }

        // 用户角色
        Long roleId = user.getRoleId();
        if (roleId == null) {
            return false;
        }

        // 判断是否有权限
        Method method = getHandleMethod(t);
        return hasAuthority(user, method);
    }

    /**
     * 获取令牌
     *
     * @param t
     * @return
     */
    private String getToken(T t) {
        String authorization = getAuthorization(t);
        if (StringUtils.isNotEmpty(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }

    public boolean hasAuthority(UserEntity user, Method method) {
        AuthorityEntity authority = authorityMap.get(method);
        if (authority == null) {
            return false;
        }
        return BooleanUtils.toBoolean(roleAuthorityService.has(user.getRoleId(), authority.getId()));
    }

    /**
     * 获取请求方法
     *
     * @param t
     * @return
     */
    protected abstract String getMethod(T t);

    /**
     * 获取请求路径
     *
     * @param t
     * @return
     */
    protected abstract String getPath(T t);

    /**
     * 获取 Authorization
     *
     * @param t
     * @return
     */
    protected abstract String getAuthorization(T t);

    /**
     * 获取处理方法
     *
     * @return
     */
    protected abstract Method getHandleMethod(T t);

    /**
     * 设置登录用户信息
     *
     * @param user
     */
    protected abstract void setUser(T t, UserEntity user);

    /**
     * 设置执行顺序为最高优先级
     *
     * @return
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // -----------入库::start-----------

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

        // -----------入库::end-----------

        List<AuthorityEntity> authorities = authorityGroups.stream()
                .map(AuthorityGroupEntity::getAuthorities)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        authorityMap = new HashMap<>(authorities.size(), 1f) {
            @Override
            public AuthorityEntity put(Object method, AuthorityEntity value) {
                return super.put(generateKey((Method) method), value);
            }

            @Override
            public AuthorityEntity get(Object method) {
                return super.get(generateKey((Method) method));
            }

            private String generateKey(Method method) {
                return Md5Util.encryptToHexString(String.format("%s.%s(%s)",
                        method.getDeclaringClass().getTypeName(),
                        method.getName(),
                        StringUtils.join(Arrays.asList(method.getParameterTypes()).stream().map(Type::getTypeName).collect(Collectors.toList()), ",")));
            }
        };

        for (AuthorityEntity authority : authorities) {
            authorityMap.put(authority.getHandleMethod(), authority);
        }
    }

}
