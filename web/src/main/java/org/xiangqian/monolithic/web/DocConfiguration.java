package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.xiangqian.monolithic.util.ResourceUtil;

import java.util.Set;

/**
 * 文档配置
 *
 * @author xiangqian
 * @date 21:23 2023/03/28
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = {"springdoc.api-docs.enabled"}, matchIfMissing = true) // 开启openapi文档条件判断
public class DocConfiguration implements BeanDefinitionRegistryPostProcessor {

    @Bean
    public GroupedOpenApi authGroupedOpenApi() {
        return buildGroupedOpenApi("auth", "org.xiangqian.monolithic.web.auth.controller");
    }

    /**
     * 创建多个 {@link GroupedOpenApi}
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> pkgs = getPkgs();
        for (String pkg : pkgs) {
            String name = pkg.substring("org.xiangqian.monolithic.web.".length(), pkg.length() - ".controller".length());
            if ("auth".equals(name)) {
                continue;
            }

            // bean name
            String beanName = name + "GroupedOpenApiFactoryBean";

            // 构建BeanDefinition
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroupedOpenApiFactoryBean.class);
            beanDefinitionBuilder.addConstructorArgValue(name);
            beanDefinitionBuilder.addConstructorArgValue(new String[]{pkg});

            // 注册BeanDefinition
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * 文档信息
     *
     * @param environment
     * @return
     */
    @Bean
    public OpenAPI openAPI(Environment environment) {
        // info
        String title = StringUtils.trimToEmpty(environment.getProperty("spring.application.name"));
        String version = StringUtils.trimToEmpty(environment.getProperty("spring.application.version"));
        String description = StringUtils.trimToEmpty(environment.getProperty("spring.application.description"));
        Contact contact = new Contact();
        contact.setEmail("xiangqian@xiangqian.org");
        contact.setName("xiangqian");
        contact.setUrl("https://github.com/xiangqians/monolithic");
        Info info = new Info()
                .title(title)
                .version(version)
                .contact(contact)
                .description(description)
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"));

        // components
        Components components = new Components();

        // How do I add authorization header in requests?
        // https://springdoc.org/index.html#how-do-i-add-authorization-header-in-requests
        // 1. You should add the @SecurityRequirement tags to your protected APIs.
        // For example:
        // @Operation(security = { @SecurityRequirement(name = "bearer-key") })
        // And the security definition sample:
        // 2.
        components.addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        // open api
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement())
                .components(components)
                .info(info)
                .externalDocs(new ExternalDocumentation().description("SpringDoc Full Documentation").url("https://springdoc.org/"));
    }

    /**
     * {@link GroupedOpenApi} 工厂bean
     */
    @AllArgsConstructor
    public static class GroupedOpenApiFactoryBean implements FactoryBean<GroupedOpenApi> {
        private String group;
        private String[] pkgs;

        @Override
        public GroupedOpenApi getObject() throws Exception {
            return buildGroupedOpenApi(group, pkgs);
        }

        @Override
        public Class<?> getObjectType() {
            return GroupedOpenApi.class;
        }
    }

    /**
     * 构建 {@link GroupedOpenApi}
     *
     * @param group 组名称
     * @param pkgs  指定扫描包路径
     * @return
     */
    private static GroupedOpenApi buildGroupedOpenApi(String group, String... pkgs) {
        return GroupedOpenApi.builder()
                .group(group) // 组名称
                .displayName(group) // 显示名称
                .pathsToMatch("/**") // 基于接口路由扫描
                .packagesToScan(pkgs) // 指定扫描包路径
                .build();
    }

    @SneakyThrows
    private static Set<String> getPkgs() {
        return ResourceUtil.getPkgs("org.xiangqian.monolithic.web.*.controller");
    }

}
