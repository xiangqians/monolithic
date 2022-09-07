package org.monolithic.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.monolithic.dao.AuthClientDetailsDao;
import org.monolithic.po.AuthClientDetailsPo;
import org.monolithic.util.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * @author xiangqian
 * @date 23:18 2022/09/06
 */
@Slf4j
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    private AuthClientDetailsDao clientDetailsDao;

    @Transactional(timeout = 10, readOnly = true)
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        log.debug("loadClient: {}", clientId);
        ClientDetails clientDetails = Optional.ofNullable(clientDetailsDao.queryByClientId(clientId)).map(ClientDetailsImpl::new).orElse(null);
        if (Objects.isNull(clientDetails)) {
            throw new NoSuchClientException(String.format("No client with requested id: %s", clientId));
        }

        log.info("clientDetails: {}", clientDetails);
        return clientDetails;
    }

    @Data
    public static class ClientDetailsImpl implements ClientDetails {
        private String clientId;
        private Set<String> resourceIds;
        private Boolean secretRequired;
        private String clientSecret;
        private Set<String> scope;
        private Set<String> authorizedGrantTypes;
        private Set<String> registeredRedirectUris;
        private Collection<GrantedAuthority> authorities;
        private Integer accessTokenValidity;
        private Integer refreshTokenValidity;
        private Boolean autoApprove;
        private Map<String, Object> additionalInformation;

        public ClientDetailsImpl(AuthClientDetailsPo po) {
            try {
                setClientId(po.getClientId());
                setResourceIds(JacksonUtils.toObject(po.getResourceIds(), new TypeReference<Set<String>>() {
                }));
                setSecretRequired("1".equals(po.getSecretRequired()) ? true : false);
                setClientSecret(po.getClientSecret());
                setScope(JacksonUtils.toObject(po.getScope(), new TypeReference<Set<String>>() {
                }));
                setAuthorizedGrantTypes(JacksonUtils.toObject(po.getAuthorizedGrantTypes(), new TypeReference<Set<String>>() {
                }));
                setRegisteredRedirectUris(JacksonUtils.toObject(po.getRegisteredRedirectUris(), new TypeReference<Set<String>>() {
                }));
                setAuthorities(Collections.emptyList());
                setAccessTokenValidity(po.getAccessTokenValidity());
                setRefreshTokenValidity(po.getRefreshTokenValidity());
                setAdditionalInformation(Optional.ofNullable(po.getAddlInfo())
                        .map(additionalInformation -> {
                            try {
                                return JacksonUtils.toObject(po.getRegisteredRedirectUris(), new TypeReference<Map<String, Object>>() {
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .orElse(null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isSecretRequired() {
            return secretRequired;
        }

        @Override
        public boolean isScoped() {
            return CollectionUtils.isNotEmpty(getScope());
        }

        @Override
        public Set<String> getRegisteredRedirectUri() {
            return registeredRedirectUris;
        }

        @Override
        public Integer getAccessTokenValiditySeconds() {
            return accessTokenValidity;
        }

        @Override
        public Integer getRefreshTokenValiditySeconds() {
            return refreshTokenValidity;
        }

        @Override
        public boolean isAutoApprove(String scope) {
            return autoApprove;
        }

    }

}
