package org.monolithic.configure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * 资源授权服务器配置
 * （spring security 核心是过滤器）
 *
 * <p>
 * 1、获取token（密码模式）
 * GET http://localhost:9000/oauth/token?client_id=monolithic&client_secret=123456&grant_type=password&scope=all&username=admin&password=123456
 * 源码：{@link TokenEndpoint}
 * <p>
 * 2、校验token
 * GET http://localhost:9000/oauth/check_token?token=4de1bfdc-b960-46ff-8e51-272e95a051c1
 * 源码：{@link CheckTokenEndpoint}
 * <p>
 * 3、刷新token
 * GET http://localhost:9000/oauth/token?client_id=monolithic&client_secret=123456&grant_type=refresh_token&refresh_token=bc1c7c28-9dea-4b37-a5ef-755fe41c6db1
 * <p>
 * {@link OAuth2AuthenticationProcessingFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 *
 * @author xiangqian
 * @date 23:41 2022/09/06
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    public UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("clientDetailsServiceImpl")
    private ClientDetailsService clientDetailsService;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 配置访问令牌端点
     *
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 认证管理器
                // 配置密码模式所需要认证管理器
                .authenticationManager(authenticationManager)
                // 设置用户验证服务
                .userDetailsService(userDetailsService)
                // 指定token的存储方式
                .tokenStore(tokenStore())
                // 允许令牌端点请求方法类型
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

        // spring Security框架默认的访问端点有：
        // 1、/oauth/authorize：获取授权码的端点
        // 2、/oauth/token：获取令牌端点。
        // 3、/oauth/confirm_access：用户确认授权提交端点。
        // 4、/oauth/error：授权服务错误信息端点。
        // 5、/oauth/check_token：用于资源服务访问的令牌解析端点。
        // 6、/oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
        // 当然如果业务要求需要改变这些默认的端点的url，也是可以修改的，AuthorizationServerEndpointsConfigurer有一个方法，如下：
        // public AuthorizationServerEndpointsConfigurer pathMapping(String defaultPath, String customPath)：
        // defaultPath：需要替换的默认端点url
        // customPath：自定义的端点url
    }

    /**
     * 客户端详情服务配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        final String permitAll = "permitAll()";
        final String isAuthenticated = "isAuthenticated()";
        security
                // 允许 /oauth/token_key 无权限访问
                .tokenKeyAccess(permitAll)

                // /oauth/check_token 需要认证权限访问
                .checkTokenAccess(isAuthenticated)

                // 允许form表单客户端认证
                // 允许客户端使用client_id和client_secret获取token
                .allowFormAuthenticationForClients();
    }

}
