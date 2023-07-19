package org.xiangqian.monolithic.doc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.xiangqian.monolithic.Application;
import org.xiangqian.monolithic.util.ResourceScanUtil;

import java.io.IOException;
import java.util.Set;

/**
 * @author xiangqian
 * @date 19:23 2023/03/28
 */
@Slf4j
@Configuration
public class DocConfiguration implements BeanDefinitionRegistryPostProcessor {

    // 默认组
    public static final String DEFAULT_GROUP = "default";

    /**
     * 默认分组OpenApi
     *
     * @return
     * @throws IOException
     */
    @Bean
    public GroupedOpenApi defaultGroupedOpenApi() {
        return buildGroupedOpenApi(DEFAULT_GROUP, getPkgs().toArray(String[]::new));
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
            // 扫描 org.xiangqian.monolithic.biz.*.controller
            String name = pkg.substring(String.format("%s.biz.", Application.BASE_PKG).length(), pkg.length() - ".controller".length());
            if (name.contains(".")) {
                name = name.replace(".", "");
            }

            // bean name
            String beanName = String.format("%s%s", name, "GroupedOpenApiFactoryBean");

            // 构建BeanDefinition
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroupedOpenApiFactoryBean.class);
            beanDefinitionBuilder.addConstructorArgValue(name);
            beanDefinitionBuilder.addConstructorArgValue(pkg);

            // 注册BeanDefinition
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public static Set<String> getPkgs() {
        try {
            return ResourceScanUtil.scanPkgs(String.format("%s.biz.*.controller", Application.BASE_PKG));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建 {@link GroupedOpenApi}
     *
     * @param group 组名称
     * @param pkgs  指定扫描包路径
     * @return
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String... pkgs) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/**") // 基于接口路由扫描
                .packagesToScan(pkgs) // 指定扫描包路径
                .build();
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
        String desc = StringUtils.trimToEmpty(environment.getProperty("spring.application.desc"));
        Contact contact = new Contact();
        contact.setEmail("xiangqian@xiangqian.org");
        contact.setName("xiangqian");
        contact.setUrl("https://github.com/xiangqians/monolithic");
        Info info = new Info()
                .title(title)
                .version(version)
                .contact(contact)
                .description(desc)
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

}
