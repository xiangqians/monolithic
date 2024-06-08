package org.xiangqian.monolithic.monitor.configuration;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiangqian
 * @date 12:34 2022/10/01
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration implements WebMvcConfigurer {

    @Value("${spring.boot.admin.secret}")
    private String secret;

    /**
     * https://github.com/codecentric/spring-boot-admin/issues/1253
     *
     * @return
     */
    @Bean
    public HttpHeadersProvider httpHeadersProvider() {
        return instance -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Secret", secret);
            return headers;
        };
    }

    // 处理静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        http
                // http授权请求配置
                .authorizeHttpRequests((authorize) -> authorize
                        // 放行登录页面
                        .requestMatchers("/login").permitAll()
                        // 放行静态资源
                        .requestMatchers("/static/**").permitAll()
                        // 放行静态资产
                        .requestMatchers("/assets/**").permitAll()
                        // /instances
                        .requestMatchers(request -> {
                            /**
                             * HTTP POST http://localhost:9000/instances
                             * {@link de.codecentric.boot.admin.server.web.InstancesController#register(de.codecentric.boot.admin.server.domain.values.Registration, org.springframework.web.util.UriComponentsBuilder)}
                             */
                            if ("POST".equals(request.getMethod())
                                    && "/instances".equals(request.getServletPath())
                                    && secret.equals(StringUtils.trim(request.getHeader("Secret")))) {
                                return true;
                            }
                            return false;
                        }).permitAll()
                        // 其他请求需要授权
                        .anyRequest().authenticated())
                // 自定义表单登录
                .formLogin(configurer -> configurer
                        // 自定义登录页面
                        .loginPage("/login")
                        // 登录成功处理器
                        .successHandler(authenticationSuccessHandler)
                        // 放行资源
                        .permitAll()
                )
                // 异常处理
                .exceptionHandling(configurer -> configurer.accessDeniedPage("/error"))
                // 登出配置
                .logout(configurer -> configurer.logoutUrl("/logout"))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(AdminServerProperties adminServerProperties) {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setTargetUrlParameter("redirectTo");
        authenticationSuccessHandler.setDefaultTargetUrl(adminServerProperties.getContextPath() + "/");
        return authenticationSuccessHandler;
    }

}
