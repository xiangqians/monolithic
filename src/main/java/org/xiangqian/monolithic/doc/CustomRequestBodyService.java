package org.xiangqian.monolithic.doc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.models.MethodAttributes;
import org.springdoc.core.models.ParameterInfo;
import org.springdoc.core.models.RequestBodyInfo;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericParameterService;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * 修改POST请求参数ref
 * {@link ParameterCustomizer#customize(io.swagger.v3.oas.models.parameters.Parameter, org.springframework.core.MethodParameter)}
 * {@link AbstractRequestService#build(org.springframework.web.method.HandlerMethod, org.springframework.web.bind.annotation.RequestMethod, io.swagger.v3.oas.models.Operation, org.springdoc.core.models.MethodAttributes, io.swagger.v3.oas.models.OpenAPI)}
 * {@link RequestBodyService#calculateRequestBodyInfo(io.swagger.v3.oas.models.Components, org.springdoc.core.models.MethodAttributes, org.springdoc.core.models.ParameterInfo, org.springdoc.core.models.RequestBodyInfo)}
 * {@link RequestBodyService#buildRequestBody(io.swagger.v3.oas.models.parameters.RequestBody, io.swagger.v3.oas.models.Components, org.springdoc.core.models.MethodAttributes, org.springdoc.core.models.ParameterInfo, org.springdoc.core.models.RequestBodyInfo)}
 * {@link GenericParameterService#calculateSchema(io.swagger.v3.oas.models.Components, org.springdoc.core.models.ParameterInfo, org.springdoc.core.models.RequestBodyInfo, com.fasterxml.jackson.annotation.JsonView)}
 * {@link RequestBodyService}
 * {@link SpringDocConfiguration#requestBodyBuilder(org.springdoc.core.service.GenericParameterService)}
 * 在注入 {@link RequestBodyService} Bean 时添加了 {@link ConditionalOnMissingBean} 注解（当 {@link RequestBodyService} Bean不存在时初始化），
 * 所以，我们可以自定义 {@link RequestBodyService} Bean
 *
 * @author xiangqian
 * @date 21:15 2023/05/12
 */
@Lazy(false)
@Component
public class CustomRequestBodyService extends RequestBodyService {

    public CustomRequestBodyService(GenericParameterService parameterBuilder) {
        super(parameterBuilder);
    }

    @Override
    public void calculateRequestBodyInfo(Components components, MethodAttributes methodAttributes, ParameterInfo parameterInfo, RequestBodyInfo requestBodyInfo) {
        super.calculateRequestBodyInfo(components, methodAttributes, parameterInfo, requestBodyInfo);
        extend(parameterInfo, requestBodyInfo);
    }

    private void extend(ParameterInfo parameterInfo, RequestBodyInfo requestBodyInfo) {
        MethodParameter methodParameter = parameterInfo.getMethodParameter();
        if (Objects.isNull(methodParameter)) {
            return;
        }

        if (!methodParameter.hasParameterAnnotation(Validated.class)) {
            return;
        }

        Validated validated = methodParameter.getParameterAnnotation(Validated.class);
        Class<?>[] groups = validated.value();
        // 只支持一个组校验文档ref
        if (ArrayUtils.isEmpty(groups) || groups.length > 1) {
            return;
        }
        Class<?> group = groups[0];

        RequestBody requestBody = requestBodyInfo.getRequestBody();
        if (Objects.isNull(requestBody)) {
            return;
        }

        Content content = requestBody.getContent();
        if (Objects.isNull(content)) {
            return;
        }

        MediaType mediaType = content.get("application/json");
        if (Objects.isNull(mediaType)) {
            return;
        }

        Schema schema = mediaType.getSchema();
        if (Objects.isNull(schema)) {
            return;
        }

        // set $ref
        String $ref = schema.get$ref().substring("#/components/schemas/".length());
        schema.set$ref(group.getSimpleName() + $ref);
    }

}
