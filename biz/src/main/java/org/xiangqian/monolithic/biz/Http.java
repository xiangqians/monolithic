package org.xiangqian.monolithic.biz;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 23:03 2024/05/30
 */
public class Http {

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    public Http(RestTemplate restTemplate, HttpHeaders headers) {
        this.restTemplate = restTemplate;
        this.headers = headers;
    }

    public <T> T delete(String url, Object requestBody, Object responseType) {
        return delete(url, headers, requestBody, responseType);
    }

    public <T> T delete(String url, HttpHeaders headers, Object requestBody, Object responseType) {
        return execute(HttpMethod.DELETE, url, null, headers, requestBody, responseType);
    }

    public <T> T put(String url, HttpHeaders headers, Object requestBody, Object responseType) {
        return execute(HttpMethod.PUT, url, null, headers, requestBody, responseType);
    }

    public <T> T post(String url, HttpHeaders headers, Object requestBody, Object responseType) {
        return execute(HttpMethod.POST, url, null, headers, requestBody, responseType);
    }

    public <T> T get(String url, Map<String, ?> paramMap, HttpHeaders headers, Object responseType) {
        if (CollectionUtils.isEmpty(paramMap)) {
            return execute(HttpMethod.GET, url, null, headers, null, responseType);
        }

        url = String.format("%s?%s", url, paramMap.keySet().stream().map(key -> String.format("%s={%s}", key, key)).collect(Collectors.joining("&")));
        return execute(HttpMethod.GET, url, paramMap, headers, null, responseType);
    }

    /**
     * http execute
     *
     * @param method       请求方法
     * @param url          请求地址
     * @param uriVariables 请求参数
     * @param headers      请求头
     * @param requestBody  请求报文
     * @param responseType 响应类型，{@link Class} | {@link ParameterizedTypeReference}
     * @param <T>
     * @return
     */
    protected <T> T execute(HttpMethod method, String url, Map<String, ?> uriVariables, HttpHeaders headers, Object requestBody, Object responseType) {
        HttpEntity<?> httpEntity = new HttpEntity(requestBody, headers);
        return execute(method, url, uriVariables, httpEntity, responseType);
    }

    /**
     * http exchange
     *
     * @param method       请求方法
     * @param url          请求地址
     * @param uriVariables 请求参数
     * @param httpEntity   请求体
     * @param responseType 响应类型，{@link Class} | {@link ParameterizedTypeReference}
     * @param <T>
     * @return
     */
    protected <T> T execute(HttpMethod method, String url, Map<String, ?> uriVariables, HttpEntity<?> httpEntity, Object responseType) {
        ResponseEntity<T> responseEntity = null;
        if (responseType instanceof Class) {
            if (uriVariables != null) {
                responseEntity = restTemplate.exchange(url, method, httpEntity, (Class<T>) responseType, uriVariables);
            } else {
                responseEntity = restTemplate.exchange(url, method, httpEntity, (Class<T>) responseType);
            }
        } else if (responseType instanceof ParameterizedTypeReference) {
            if (uriVariables != null) {
                responseEntity = restTemplate.exchange(url, method, httpEntity, (ParameterizedTypeReference<T>) responseType, uriVariables);
            } else {
                responseEntity = restTemplate.exchange(url, method, httpEntity, (ParameterizedTypeReference<T>) responseType);
            }
        }

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throw new RuntimeException(statusCode.value() + "");
        }

        T body = responseEntity.getBody();
        return body;
    }

}
