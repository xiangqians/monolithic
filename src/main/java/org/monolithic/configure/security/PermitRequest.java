package org.monolithic.configure.security;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Set;

/**
 * 许可请求实体类
 *
 * @author xiangqian
 * @date 21:07 2022/09/07
 */
@Data
public class PermitRequest {

    private HttpMethod httpMethod;
    private Set<String> patterns;

    public PermitRequest(HttpMethod httpMethod, Set<String> patterns) {
        this.httpMethod = httpMethod;
        this.patterns = patterns;
    }

    public PermitRequest(HttpMethod httpMethod, String... patterns) {
        this(httpMethod, Set.of(patterns));
    }

    public PermitRequest(String... patterns) {
        this(null, patterns);
    }

}
