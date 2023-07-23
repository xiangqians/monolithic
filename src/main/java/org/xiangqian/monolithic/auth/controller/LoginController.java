package org.xiangqian.monolithic.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 自定义登陆页
 * <p>
 * OAuth2AuthorizationEndpointFilter
 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter#doFilterInternal(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, jakarta.servlet.FilterChain)}
 * <p>
 * 重定向到 /login
 * {@link org.springframework.security.web.access.intercept.AuthorizationFilter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)}
 * {@link org.springframework.security.web.access.ExceptionTranslationFilter#handleSpringSecurityException(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, jakarta.servlet.FilterChain, java.lang.RuntimeException)}
 * {@link org.springframework.security.web.access.ExceptionTranslationFilter#handleAccessDeniedException(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, jakarta.servlet.FilterChain, org.springframework.security.access.AccessDeniedException)}
 * {@link org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint#commence(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)}
 * <p>
 * /login
 * {@link org.springframework.security.web.context.SecurityContextHolderFilter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)}
 * {@link org.springframework.security.web.FilterChainProxy.VirtualFilterChain#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse)}
 * {@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)}
 * {@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter#generateLoginPageHtml(jakarta.servlet.http.HttpServletRequest, boolean, boolean)}
 * {@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter#DefaultLoginPageGeneratingFilter()}
 * {@link org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer#DefaultLoginPageConfigurer()}
 * {@link org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration#httpSecurity()}
 *
 * @author xiangqian
 * @date 19:22 2023/07/19
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

}
