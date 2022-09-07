package org.monolithic.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Optional;

/**
 * {@link org.springframework.security.core.authority.AuthorityUtils}
 *
 * @author xiangqian
 * @date 22:02 2022/09/07
 */
public class AuthorityUtils {

    private static final String ROLE_GRANTED_AUTHORITY_PREFIX = "ROLE_";
    private static final String PERM_GRANTED_AUTHORITY_PREFIX = "PERM_";

    public static boolean hasPerm(String perm) {
        String value = PERM_GRANTED_AUTHORITY_PREFIX + perm;
        return Optional.ofNullable(getAuthorities())
                .map(authorities -> authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(value)))
                .orElse(false);
    }

    public static boolean hasRole(String role) {
        String value = ROLE_GRANTED_AUTHORITY_PREFIX + role;
        return Optional.ofNullable(getAuthorities())
                .map(authorities -> authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(value)))
                .orElse(false);
    }

    public static GrantedAuthority createGrantedAuthorityByPerm(String perm) {
        return new SimpleGrantedAuthority(PERM_GRANTED_AUTHORITY_PREFIX + perm);
    }

    public static GrantedAuthority createGrantedAuthorityByRole(String role) {
        return new SimpleGrantedAuthority(ROLE_GRANTED_AUTHORITY_PREFIX + role);
    }

    public static User getUser() {
        Authentication authentication = getAuthentication();
        Assert.notNull(authentication, "Authentication为空！");
        Object principal = authentication.getPrincipal();
        return principal instanceof User ? (User) principal : null;
    }

    public static String getAccessToken() {
        return getAuthenticationDetails().getTokenValue();
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthentication().getAuthorities();
    }

    public static OAuth2AuthenticationDetails getAuthenticationDetails() {
        return (OAuth2AuthenticationDetails) getAuthentication().getDetails();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
