package org.xiangqian.monolithic.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * see {@link org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2EndpointUtils}
 *
 * @author xiangqian
 * @date 20:07 2023/07/21
 */
public class OAuth2EndpointUtil {

    public static final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                String[] var3 = values;
                int var4 = values.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    String value = var3[var5];
                    parameters.add(key, value);
                }
            }

        });
        return parameters;
    }

    public static Map<String, Object> getParametersIfMatchesAuthorizationCodeGrantRequest(HttpServletRequest request, String... exclusions) {
        if (!matchesAuthorizationCodeGrantRequest(request)) {
            return Collections.emptyMap();
        } else {
            MultiValueMap<String, String> multiValueParameters = getParameters(request);
            String[] var3 = exclusions;
            int var4 = exclusions.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String exclusion = var3[var5];
                multiValueParameters.remove(exclusion);
            }

            Map<String, Object> parameters = new HashMap();
            multiValueParameters.forEach((key, value) -> {
                parameters.put(key, value.size() == 1 ? value.get(0) : value.toArray(new String[0]));
            });
            return parameters;
        }
    }

    public static boolean matchesAuthorizationCodeGrantRequest(HttpServletRequest request) {
        return AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(request.getParameter("grant_type")) && request.getParameter("code") != null;
    }

    public static boolean matchesPkceTokenRequest(HttpServletRequest request) {
        return matchesAuthorizationCodeGrantRequest(request) && request.getParameter("code_verifier") != null;
    }

    public static void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthenticationException(error);
    }

    public static String normalizeUserCode(String userCode) {
        Assert.hasText(userCode, "userCode cannot be empty");
        StringBuilder sb = new StringBuilder(userCode.toUpperCase().replaceAll("[^A-Z\\d]+", ""));
        Assert.isTrue(sb.length() == 8, "userCode must be exactly 8 alpha/numeric characters");
        sb.insert(4, '-');
        return sb.toString();
    }

}
