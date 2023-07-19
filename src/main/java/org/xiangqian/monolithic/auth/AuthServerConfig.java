package org.xiangqian.monolithic.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.xiangqian.monolithic.util.KeyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * auth配置
 * <p>
 * Spring Authorization Server requires a Java 17 or higher Runtime Environment.
 * <p>
 * Spring Boot中文社区：https://springdoc.cn/spring-authorization-server
 * 文档：https://docs.spring.io/spring-authorization-server/docs/current/reference/html/index.html
 * <p>
 * 默认配置：
 * {@link OAuth2AuthorizationServerConfiguration} 是一个 @Configuration，为OAuth2授权服务器提供最小的默认配置。
 * {@link OAuth2AuthorizationServerConfiguration} 使用 {@link OAuth2AuthorizationServerConfigurer} 来应用默认配置，
 * 并注册了一个由支持OAuth2授权服务器的所有基础设施组件组成的 {@link SecurityFilterChain} @Bean。
 * <p>
 * {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(org.springframework.security.config.annotation.web.builders.HttpSecurity)} 是一个方便（静态）的实用方法，它将默认的 OAuth2 security configuration 应用到 {@link HttpSecurity} 。
 *
 * @author xiangqian
 * @date 19:43 2023/05/18
 */
@EnableWebSecurity // 加载WebSecurityConfiguration配置类，配置安全认证策略；加载AuthenticationConfiguration，配置了认证信息；
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity(prePostEnabled = true, // 开启预处理验证
        jsr250Enabled = true, // 启用JSR250注解支持
        securedEnabled = true) // 启用 {@link org.springframework.security.access.annotation.Secured} 注解支持
public class AuthServerConfig {

    @Value("${server.port}")
    private int port;

    /**
     * step：1
     * 用于协议端点的 Spring Security 过滤器链
     * {@link OAuth2AuthorizationServerConfiguration#authorizationServerSecurityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 将默认的 OAuth2 security configuration 应用到 HttpSecurity
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // Enable OpenID Connect 1.0
                .oidc(Customizer.withDefaults())
                // 设置自定义用户确认授权页
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage("/oauth2/consent"));

        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * step：2
     * 用于认证的 Spring Security 过滤器链
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // http授权请求配置
                .authorizeHttpRequests((authorize) -> authorize
                        // 放行静态资源
                        .requestMatchers("/assets/**", "/webjars/**", "/login").permitAll()
                        // 其他请求需要授权
                        .anyRequest().authenticated())
                // Form login handles the redirect to the login page from the authorization server filter chain
//                .formLogin(Customizer.withDefaults())
                .formLogin(formLogin -> formLogin.loginPage("/login")); // 自定义登陆页

        // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token？？？
//        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * step：3
     * 用于校验要认证的用户
     *
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails userDetails = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "TEST")
                .authorities("app", "web", "/test2", "/test3")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    /**
     * step：4
     * 用于管理客户端（oauth2_registered_client）
     *
     * @param jdbcTemplate
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        List<RegisteredClient> clients = new ArrayList<>(2);

        // oidc客户端
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // 客户端id
                .clientId("oidc-client")
                // 客户端秘钥
                .clientSecret("{noop}secret")
                // 客户端认证方式
                // 1、CLIENT_SECRET_BASIC（基于请求头认证）
                // 2、CLIENT_SECRET_POST
                // 3、CLIENT_SECRET_JWT
                // 4、PRIVATE_KEY_JWT
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // OAuth2.0授权方式
                // 配置资源服务器使用该客户端获取授权时支持的方式
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 授权码
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // 客户端凭证
                .authorizationGrantType(AuthorizationGrantType.PASSWORD) // 密码方式
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // 刷新token
                // 授权码模式回调地址
                .redirectUri(String.format("http://127.0.0.1:%s/login/oauth2/code/%s", port, "oidc-client"))
                .redirectUri(String.format("http://127.0.0.1:%s/authorized", port))
                // 处理logout后重定向到指定地址
                .postLogoutRedirectUri(String.format("http://127.0.0.1:%s/logged-out", port))
                // 该客户端授权范围
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                // 自定义scope
//                .scope("all") // read write
                .scope("read")
                .scope("write")
                // client请求访问时需要授权同意
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        clients.add(oidcClient);

        // 设备码授权客户端
        RegisteredClient deviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("device-client")
                // 公共客户端
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                // 设备码授权
                .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope("read")
                .scope("write")
                .build();
        clients.add(deviceClient);

        // Save registered client's in db as if in-memory
        RegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        for (RegisteredClient client : clients) {
            RegisteredClient storedClient = registeredClientRepository.findByClientId(client.getClientId());
            if (Objects.nonNull(storedClient)) {
                client = RegisteredClient.from(client).id(storedClient.getId()).build();
            }
            registeredClientRepository.save(client);
        }

        return registeredClientRepository;
    }

    /**
     * 配置oauth2授权管理服务（oauth2_authorization）
     *
     * @param jdbcTemplate
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 配置授权确认管理服务（oauth2_authorization_consent）
     *
     * @param jdbcTemplate
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * step：5
     * 用于签署访问令牌（access token）
     * <p>
     * 加载JWK资源
     * JWT：指的是 JSON Web Token，不存在签名的JWT是不安全的，存在签名的JWT是不可窜改的
     * JWS：指的是签过名的JWT，即拥有签名的JWT
     * JWK：既然涉及到签名，就涉及到签名算法，对称加密还是非对称加密，那么就需要加密的 密钥或者公私钥对。此处我们将 JWT的密钥或者公私钥对统一称为 JSON WEB KEY，即 JWK。
     *
     * @return
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = KeyUtil.genRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * step：6
     * 用于解码签名访问令牌（access token）
     *
     * @param jwkSource
     * @return
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * step：7
     * 用于配置Spring授权服务器，设置jwt签发者、默认端点请求地址等
     *
     * @return
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * 密码解析器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt
        return new BCryptPasswordEncoder();
    }

}


// https://zhuanlan.zhihu.com/p/597701860
// https://juejin.cn/post/7199456238190870585
// https://www.cnblogs.com/Zhang-Xiang/p/16254474.html
// https://blog.csdn.net/weixin_43356507/article/details/131006763

