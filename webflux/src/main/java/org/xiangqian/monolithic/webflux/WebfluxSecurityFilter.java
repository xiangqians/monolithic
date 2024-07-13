package org.xiangqian.monolithic.webflux;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.web.WebSecurityFilter;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author xiangqian
 * @date 12:00 2024/07/07
 */
@Component
public class WebfluxSecurityFilter extends WebSecurityFilter<ServerWebExchange> implements WebFilter {

    public static final String PRINCIPAL_ATTRIBUTE = "PRINCIPAL";

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 移除PRINCIPAL属性
        Map<String, Object> attributes = exchange.getAttributes();
        attributes.remove(PRINCIPAL_ATTRIBUTE);

        try {
            // 是否放行
            if (filter(exchange)) {
                // 下一个过滤器
                return chain.filter(exchange);
            }
        } catch (GetHandleMethodException e) {
            return requestMappingHandlerMapping.getHandler(exchange).flatMap(object -> {
                if (object instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) object;
                    if (hasAuthority(exchange.getAttribute(PRINCIPAL_ATTRIBUTE), handlerMethod.getMethod())) {
                        // 下一个过滤器
                        return chain.filter(exchange);
                    }
                }

                // 未授权
                return writeAndFlush(exchange, new Result<>(Code.UNAUTHORIZED));
            });
        }

        // 未授权
        return writeAndFlush(exchange, new Result<>(Code.UNAUTHORIZED));
    }

    @Override
    protected String getMethod(ServerWebExchange exchange) {
        return exchange.getRequest().getMethod().name();
    }

    @Override
    protected String getPath(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value();
    }

    @Override
    protected String getAuthorization(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return StringUtils.trim(authorization);
    }

    @Override
    protected Method getHandleMethod(ServerWebExchange exchange) {
        throw new GetHandleMethodException();
    }

    @Override
    protected void setUser(ServerWebExchange exchange, UserEntity user) {
        Map<String, Object> attributes = exchange.getAttributes();
        attributes.put(PRINCIPAL_ATTRIBUTE, user);
    }

    @SneakyThrows
    public static Mono<Void> writeAndFlush(ServerWebExchange exchange, Result<?> result) {
        ServerHttpResponse response = exchange.getResponse();

        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        response.setStatusCode(HttpStatus.OK);

        byte[] bytes = JsonUtil.serializeAsBytes(result);

//        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));

        DataBuffer dataBuffer = response.bufferFactory().allocateBuffer(bytes.length).write(bytes);
        return response.writeAndFlushWith(Mono.just(ByteBufMono.just(dataBuffer)));
    }

    public static class GetHandleMethodException extends RuntimeException {
    }

}
