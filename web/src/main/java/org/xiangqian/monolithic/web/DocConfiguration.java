package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.xiangqian.monolithic.biz.ResourceUtil;

import java.io.IOException;

/**
 * 文档配置
 *
 * @author xiangqian
 * @date 21:23 2023/03/28
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = {"springdoc.api-docs.enabled"}, matchIfMissing = true) // 开启openapi文档条件判断
public class DocConfiguration {

    /**
     * 默认分组
     *
     * @return
     * @throws IOException
     */
    @Bean
    public GroupedOpenApi groupedOpenApi() throws IOException {
        String group = "default";
        String[] pkgs = ResourceUtil.getPkgs("org.xiangqian.monolithic.web.*.controller").toArray(String[]::new);
        return GroupedOpenApi.builder()
                .group(group).displayName(group) // 组名称
                .pathsToMatch("/**") // 匹配接口路径
                .packagesToScan(pkgs) // 扫描包路径
                .build();
    }

    /**
     * 文档信息
     *
     * @param name
     * @param version
     * @param description
     * @return
     */
    @Bean
    public OpenAPI openAPI(@Value("${spring.application.name}") String name,
                           @Value("${spring.application.version}") String version,
                           @Value("${spring.application.description}") String description) {
        Contact contact = new Contact();
        contact.setEmail("xiangqian@xiangqian.org");
        contact.setName("xiangqian");
        contact.setUrl("https://github.com/xiangqians/microservices");
        Info info = new Info()
                .title(name)
                .version(version)
                .contact(contact)
                .description(description)
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"));

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

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(components)
                .info(info)
                .externalDocs(new ExternalDocumentation().description("SpringDoc Full Documentation").url("https://springdoc.org/"));
    }

}
