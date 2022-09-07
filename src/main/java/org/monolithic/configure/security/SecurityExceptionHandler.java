package org.monolithic.configure.security;

import lombok.extern.slf4j.Slf4j;
import org.monolithic.resp.DefaultStatusCode;
import org.monolithic.resp.Response;
import org.monolithic.resp.StatusCode;
import org.monolithic.util.JacksonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Security异常处理器
 * <p>
 * Authentication返回统一格式
 * 源码：
 * {@link AuthenticationEntryPoint}
 * {@link OAuth2AuthenticationEntryPoint}
 * {@link ResourceServerSecurityConfigurer#authenticationEntryPoint}
 * {@link ResourceServerConfiguration#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
 * {@link ResourceServerConfiguration#configure(ResourceServerSecurityConfigurer)}
 * <p>
 * {@link WebResponseExceptionTranslator}
 * {@link DefaultWebResponseExceptionTranslator}
 *
 * @author xiangqian
 * @date 23:09 2022/09/06
 */
@Slf4j
@Component
public class SecurityExceptionHandler extends OAuth2AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        StatusCode statusCode = null;
        String message = null;

        // invalid token
        if (cause instanceof InvalidTokenException) {
            statusCode = DefaultStatusCode.INVALID_TOKEN;
            message = cause.getMessage();
        }
        // unauthorized
        else {
            statusCode = DefaultStatusCode.UNAUTHORIZED;
            message = Optional.ofNullable(cause).map(Throwable::getMessage).orElse(authException.getMessage());
        }

        // write
        response.getWriter().write(JacksonUtils.toJson(Response.builder()
                .statusCode(statusCode)
                .message(message)
                .build()));
    }

}
