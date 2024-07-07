package org.xiangqian.monolithic.webflux;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.web.WebSecurityFilter;
import reactor.core.publisher.Mono;

/**
 * @author xiangqian
 * @date 12:00 2024/07/07
 */
@Component
public class WebfluxSecurityFilter extends WebSecurityFilter<ServerWebExchange> implements WebFilter {

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 是否放行
        if (filter(exchange)) {
            // 下一个过滤器
            return chain.filter(exchange);
        }

        // 未授权
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JsonUtil.serializeAsBytes(new Result<>(Code.UNAUTHORIZED)))));
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
        return StringUtils.trim(headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

}
