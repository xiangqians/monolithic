package org.xiangqian.monolithic.doc;

import org.apache.commons.collections4.CollectionUtils;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 自定义 {@link SwaggerUiConfigParameters}
 * <p>
 * GET /v3/api-docs/swagger-config
 * {@link SwaggerConfigResource#openapiJson(jakarta.servlet.http.HttpServletRequest)}
 * {@link SwaggerUiConfigParameters#getConfigParameters()}
 * <p>
 * see {@link SwaggerConfig#swaggerUiConfigParameters(org.springdoc.core.properties.SwaggerUiConfigProperties)}
 * see {@link SwaggerUiConfigParameters}
 *
 * @author xiangqian
 * @date 21:06 2023/05/12
 */
@Lazy(false)
@Component
public class CustomSwaggerUiConfigParameters extends SwaggerUiConfigParameters {

    public CustomSwaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfig) {
        super(swaggerUiConfig);
    }

    @Override
    public Map<String, Object> getConfigParameters() {
        Map<String, Object> params = super.getConfigParameters();

        // 将 default 组设置为第一个
        Set<SwaggerUrl> urls = (Set<SwaggerUrl>) params.get(URLS_PROPERTY);
        if (CollectionUtils.isNotEmpty(urls)) {
            SwaggerUrl defaultSwaggerUrl = urls.stream().filter(swaggerUrl -> DocConfiguration.DEFAULT_GROUP.equals(swaggerUrl.getName())).findFirst().orElse(null);
            Set<SwaggerUrl> newUrls = new LinkedHashSet<>();
            if (Objects.nonNull(defaultSwaggerUrl)) {
                newUrls.add(defaultSwaggerUrl);
            }
            for (SwaggerUrl swaggerUrl : urls) {
                if (DocConfiguration.DEFAULT_GROUP.equals(swaggerUrl.getName())) {
                    continue;
                }
                newUrls.add(swaggerUrl);
            }
            params.put(URLS_PROPERTY, newUrls);
        }

        return params;
    }

}
