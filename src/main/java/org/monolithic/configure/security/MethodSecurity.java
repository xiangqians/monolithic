package org.monolithic.configure.security;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.monolithic.annotation.PubRes;
import org.monolithic.configure.DocumentConfiguration;
import org.monolithic.constant.Role;
import org.monolithic.service.PermService;
import org.monolithic.util.AuthorityUtils;
import org.monolithic.util.ClassPathScanningProvider;
import org.monolithic.util.PreAuthorizeUtils;
import org.monolithic.vo.perm.PermVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * Method Security
 * <p>
 * see:
 * {@link SecurityExpressionOperations}
 *
 * @author xiangqian
 * @date 21:40 2022/09/07
 */
@Component("pre")
public class MethodSecurity {

    @Autowired
    private PermService permService;

    private Set<String> resourcePermitPatterns;

    @PostConstruct
    public void init() throws IOException {
        // 公共资源
        ClassPathScanningProvider classPathScanningProvider = new ClassPathScanningProvider();
        Set<Class<?>> classSet = classPathScanningProvider.findClassSet(clazz -> (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class))
                        && clazz.isAnnotationPresent(RequestMapping.class)
                        && clazz.isAnnotationPresent(Api.class),
                DocumentConfiguration.BASE_PACKAGES);
        for (Class<?> clazz : classSet) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PreAuthorize.class)
                        && method.isAnnotationPresent(ApiOperation.class)
                        && method.isAnnotationPresent(PubRes.class)) {
                    PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
                    String path = PreAuthorizeUtils.getValue(preAuthorize);
                    resourcePermitPatterns.add(path);
                }
            }
        }
    }

    public boolean hasPerm(String path) {
        // 资源允许访问
        if (Objects.nonNull(resourcePermitPatterns) && resourcePermitPatterns.contains(path)) {
            return true;
        }

        // 系统管理员无需授权
        if (AuthorityUtils.hasRole(Role.SYS_ADMIN)) {
            return true;
        }

        // query
        PermVo permVo = permService.queryByPath(path);
        if (Objects.isNull(permVo)) {
            return true;
        }
        return AuthorityUtils.hasPerm(String.valueOf(permVo.getId()));
    }

    public boolean hasRole(String role) {
        return AuthorityUtils.hasRole(role);
    }

}
