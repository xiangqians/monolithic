package org.xiangqian.monolithic.auth;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 密码模式认证Token
 *
 * @author xiangqian
 * @date 19:48 2023/07/21
 */
public class UsernamePasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private Set<String> scopes;

    public UsernamePasswordAuthenticationToken(Authentication clientPrincipal, @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {
        super(AuthorizationGrantType.PASSWORD, clientPrincipal, additionalParameters);
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet(scopes) : Collections.emptySet());
    }

    public Set<String> getScopes() {
        return this.scopes;
    }

}
