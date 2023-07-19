package org.xiangqian.monolithic.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.discoverer.SpringDocParameterNameDiscoverer;
import org.springdoc.core.models.MethodAttributes;
import org.springdoc.core.service.GenericParameterService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.service.RequestBodyService;
import org.springdoc.core.utils.ClassInfo;
import org.springdoc.core.utils.FieldInfo;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 修改Get请求参数ref
 * {@link ParameterCustomizer#customize(io.swagger.v3.oas.models.parameters.Parameter, org.springframework.core.MethodParameter)}
 * {@link RequestService#customiseParameter(io.swagger.v3.oas.models.parameters.Parameter, org.springdoc.core.models.ParameterInfo, java.util.List)}
 * {@link RequestService#build(org.springframework.web.method.HandlerMethod, org.springframework.web.bind.annotation.RequestMethod, io.swagger.v3.oas.models.Operation, org.springdoc.core.models.MethodAttributes, io.swagger.v3.oas.models.OpenAPI)}
 * {@link SpringDocWebMvcConfiguration#requestBuilder(org.springdoc.core.service.GenericParameterService, org.springdoc.core.service.RequestBodyService, org.springdoc.core.service.OperationService, java.util.Optional, org.springdoc.core.discoverer.SpringDocParameterNameDiscoverer)}
 * 在注入 {@link RequestService} Bean 时添加了 {@link ConditionalOnMissingBean} 注解（当 {@link RequestService} Bean不存在时初始化），
 * 所以，我们可以自定义 {@link RequestService} Bean
 *
 * @author xiangqian
 * @date 21:13 2023/05/12
 */
@Lazy(false)
@Component
public class CustomRequestService extends RequestService {

    public CustomRequestService(GenericParameterService parameterBuilder, RequestBodyService requestBodyService, OperationService operationService, Optional<List<ParameterCustomizer>> parameterCustomizers, SpringDocParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        super(parameterBuilder, requestBodyService, operationService, parameterCustomizers, localSpringDocParameterNameDiscoverer);
    }

    @Override
    public Operation build(HandlerMethod handlerMethod, RequestMethod requestMethod, Operation operation, MethodAttributes methodAttributes, OpenAPI openAPI) {
        operation = super.build(handlerMethod, requestMethod, operation, methodAttributes, openAPI);
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isNotEmpty(parameters)) {
            try {
                extend(handlerMethod, parameters);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return operation;
    }

    private void extend(HandlerMethod handlerMethod, List<Parameter> parameters) throws Exception {
        List<MethodParameter> methodParameters = Arrays.stream(handlerMethod.getMethodParameters())
                .filter(methodParameter -> methodParameter.hasParameterAnnotation(ParameterObject.class)
                        && methodParameter.hasParameterAnnotation(Validated.class)
                        && ArrayUtils.isNotEmpty(methodParameter.getParameterAnnotation(Validated.class).value()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(methodParameters)) {
            return;
        }

        Map<Class<?>, Set<FieldInfo>> gfsMap = new HashMap<>(16, 1f);
        for (MethodParameter methodParameter : methodParameters) {
            // c
            Class<?> c = methodParameter.getParameterType();

            // class info
            ClassInfo classInfo = ClassInfo.get(c);
            if (Objects.isNull(classInfo)) {
                continue;
            }

            // group map
            Map<Class<?>, Set<FieldInfo>> candidateGfsMap = classInfo.getGfsMap();
            if (MapUtils.isEmpty(candidateGfsMap)) {
                continue;
            }

            Validated validated = methodParameter.getParameterAnnotation(Validated.class);
            Class<?>[] groups = validated.value();
            for (Class<?> group : groups) {
                Set<FieldInfo> candidateFieldInfos = candidateGfsMap.get(group);
                if (CollectionUtils.isEmpty(candidateFieldInfos)) {
                    continue;
                }

                Set<FieldInfo> fieldInfos = gfsMap.get(group);
                if (Objects.isNull(fieldInfos)) {
                    gfsMap.put(group, candidateFieldInfos);
                } else {
                    fieldInfos.addAll(candidateFieldInfos);
                }
            }
        }

        if (MapUtils.isEmpty(gfsMap)) {
            return;
        }

        // <Name, FieldInfo> map
        Map<String, FieldInfo> fieldInfoMap = gfsMap.values()
                .stream().flatMap(Collection::stream).collect(Collectors.toSet())
                .stream().collect(Collectors.toMap(FieldInfo::getName, Function.identity(), (o1, o2) -> o2));

        // iterator
        Iterator<Parameter> iterator = parameters.iterator();
        while (iterator.hasNext()) {
            Parameter parameter = iterator.next();
            String name = parameter.getName();
            if (!fieldInfoMap.containsKey(name)) {
                iterator.remove();
            }
        }
    }

}
