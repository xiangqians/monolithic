package org.xiangqian.monolithic.doc;

/**
 * customizer
 * https://github.com/springdoc/springdoc-openapi/tree/master/springdoc-openapi-common/src/main/java/org/springdoc/core/customizers
 * <p>
 * 如何自定义一个实体类参数呢？
 * {@link org.springdoc.core.customizers.PropertyCustomizer#customize(io.swagger.v3.oas.models.media.Schema, io.swagger.v3.core.converter.AnnotatedType)}
 * {@link org.springdoc.api.AbstractOpenApiResource#calculatePath(org.springframework.web.method.HandlerMethod, org.springdoc.core.fn.RouterOperation, java.util.Locale, io.swagger.v3.oas.models.OpenAPI)}
 * {@link org.springdoc.core.service.AbstractRequestService#build(org.springframework.web.method.HandlerMethod, org.springframework.web.bind.annotation.RequestMethod, io.swagger.v3.oas.models.Operation, org.springdoc.core.models.MethodAttributes, io.swagger.v3.oas.models.OpenAPI)}
 * {@link org.springdoc.core.service.AbstractRequestService#buildParams(org.springdoc.core.models.ParameterInfo, io.swagger.v3.oas.models.Components, org.springframework.web.bind.annotation.RequestMethod, com.fasterxml.jackson.annotation.JsonView, java.lang.String)}
 * {@link org.springdoc.core.service.AbstractRequestService#buildParam(org.springdoc.core.models.ParameterInfo, io.swagger.v3.oas.models.Components, com.fasterxml.jackson.annotation.JsonView)}
 * {@link org.springdoc.core.service.GenericParameterService#calculateSchema(io.swagger.v3.oas.models.Components, org.springdoc.core.models.ParameterInfo, org.springdoc.core.models.RequestBodyInfo, com.fasterxml.jackson.annotation.JsonView)}
 * {@link org.springdoc.core.utils.SpringDocAnnotationsUtils#extractSchema(io.swagger.v3.oas.models.Components, java.lang.reflect.Type, com.fasterxml.jackson.annotation.JsonView, java.lang.annotation.Annotation[])}
 * <p>
 * {@link io.swagger.v3.oas.models.Components#getSchemas()}
 * {@link org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource#openapiJson(jakarta.servlet.http.HttpServletRequest, java.lang.String, java.lang.String, java.util.Locale)}
 * {@link org.springdoc.webmvc.api.OpenApiResource#openapiJson(jakarta.servlet.http.HttpServletRequest, java.lang.String, java.util.Locale)}
 * {@link io.swagger.v3.core.filter.SpecFilter#removeBrokenReferenceDefinitions(io.swagger.v3.oas.models.OpenAPI)}
 *
 * @author xiangqian
 * @date 21:12 2023/05/12
 */
public class DocCustomizer {
}
