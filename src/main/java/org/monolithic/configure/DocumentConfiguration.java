package org.monolithic.configure;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.monolithic.annotation.DocketGroup;
import org.monolithic.annotation.DocketGroups;
import org.monolithic.util.ClassPathScanningProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 22:06 2022/08/15
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile({"dev", "test"}) // 仅在 dev、test环境下开启swagger文档
public class DocumentConfiguration implements BeanDefinitionRegistryPostProcessor {

    // 文档标题
    private static final String TITLE = "Monolithic API";
    // 文档描述
    private static final String DESCRIPTION = "Monolithic";

    // 组名称
    private static final String DEFAULT_GROUP_NAME = "default";
    // 基础包
    public static final String[] BASE_PACKAGES = {"org.monolithic.controller"};

    /* >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 扩展 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> */

    /**
     * <groupName, <path, tags>>
     */
    private Map<String, Map<String, String[]>> groupNameMap;

    public DocumentConfiguration() {
        try {
            initGroupNameMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //    @Bean
    public CustomPlugin customPlugin() {
        return new CustomPlugin();
    }

    public class CustomPlugin implements ModelPropertyBuilderPlugin, ParameterBuilderPlugin, OperationBuilderPlugin, ModelBuilderPlugin {

        @Override
        public void apply(ModelPropertyContext context) {
            // 获取当前字段的类型
            Class fieldType = context.getBeanPropertyDefinition().get().getField().getRawType();
        }

        @Override
        public void apply(OperationContext context) {
        }

        /**
         * {@link DocumentationPluginsManager#parameter(springfox.documentation.spi.service.contexts.ParameterContext)}
         * {@link ParameterBuilder#build()}
         *
         * @param context
         */
        @Override
        public void apply(ParameterContext context) {

            // 获取擦除类型
            Class<?> type = context.resolvedMethodParameter().getParameterType().getErasedType();
            if (Enum.class.isAssignableFrom(type)) {
                ParameterBuilder parameterBuilder = context.parameterBuilder();
                AllowableListValues values = new AllowableListValues(Lists.newArrayList("1", "2", "3"), "LIST");
//                parameterBuilder.allowableValues(values);
            }
        }

        @Override
        public void apply(ModelContext context) {
        }

        @Override
        public boolean supports(DocumentationType documentationType) {
            return true;
        }
    }

    @Bean
    @Primary
    public ServiceModelToSwagger2Mapper customServiceModelToSwagger2MapperImpl() {
        return new CustomServiceModelToSwagger2MapperImpl();
    }

    public class CustomServiceModelToSwagger2MapperImpl extends ServiceModelToSwagger2MapperImpl {

        private ThreadLocal<String> groupNameThreadLocal;
        private ThreadLocal<ApiDescription> apiDescriptionThreadLocal;


        public CustomServiceModelToSwagger2MapperImpl() {
            groupNameThreadLocal = ThreadLocal.withInitial(() -> null);
            apiDescriptionThreadLocal = ThreadLocal.withInitial(() -> null);
        }

        @Override
        public Swagger mapDocumentation(Documentation from) {
            try {
                String groupName = from.getGroupName();
                groupNameThreadLocal.set(groupName);
                Swagger swagger = super.mapDocumentation(from);
                if (groupNameMap.containsKey(groupName)) {
                    swagger.setTags(Collections.emptyList());
                }
                return swagger;
            } finally {
                groupNameThreadLocal.remove();
            }
        }

        /**
         * see {@link ServiceModelToSwagger2Mapper#mapOperations(springfox.documentation.service.ApiDescription, java.util.Optional, springfox.documentation.service.ModelNamesRegistry)}
         *
         * @param api
         * @param existingPath
         * @param modelNamesRegistry
         * @return
         */
        private Path mapOperations(ApiDescription api, Optional<Path> existingPath, ModelNamesRegistry modelNamesRegistry) {
            Path path = existingPath.orElse(new Path());
            Iterator var5 = BuilderDefaults.nullToEmptyList(api.getOperations()).iterator();
            while (var5.hasNext()) {
                springfox.documentation.service.Operation each = (springfox.documentation.service.Operation) var5.next();
                Operation operation = this.mapOperation(each, modelNamesRegistry);
                path.set(each.getMethod().toString().toLowerCase(), operation);
            }
            return path;
        }

        @Override
        protected Map<String, Path> mapApiListings(Map<String, List<ApiListing>> apiListings) {
            Map<String, Path> paths = new TreeMap();
            apiListings.values().stream().flatMap(Collection::stream).forEachOrdered((each) -> {
                Iterator var3 = each.getApis().iterator();
                while (var3.hasNext()) {
                    ApiDescription api = (ApiDescription) var3.next();
                    try {
                        apiDescriptionThreadLocal.set(api);
                        log.debug("Mapping operation with path {}", api.getPath());
                        paths.put(api.getPath(), this.mapOperations(api, Optional.ofNullable(paths.get(api.getPath())), each.getModelNamesRegistry()));
                    } finally {
                        apiDescriptionThreadLocal.remove();
                    }
                }
            });
            return paths;
        }

        @Override
        protected Operation mapOperation(springfox.documentation.service.Operation from, ModelNamesRegistry modelNames) {
            Operation operation = super.mapOperation(from, modelNames);
            Set<String> tagSet = from.getTags();
            Map<String, String[]> pathMap = null;
            if (CollectionUtils.isEmpty(tagSet) || MapUtils.isEmpty((pathMap = groupNameMap.get(groupNameThreadLocal.get())))) {
                return operation;
            }

            ApiDescription apiDescription = apiDescriptionThreadLocal.get();

            List<String> tags = Optional.ofNullable(pathMap.get(apiDescription.getPath()))
                    .map(array -> Arrays.stream(array).filter(StringUtils::isNotEmpty).collect(Collectors.toList()))
                    .orElse(null);

            if (CollectionUtils.isNotEmpty(tags)) {
                operation.setTags(tags);
            }
            return operation;
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if (MapUtils.isEmpty(groupNameMap)) {
            return;
        }

        int index = 0;
        for (Map.Entry<String, Map<String, String[]>> entry : groupNameMap.entrySet()) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(AntPatternDocketImpl.class);
            ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addIndexedArgumentValue(0, entry.getKey());
            constructorArgumentValues.addIndexedArgumentValue(1, entry.getValue().keySet().stream().toArray(String[]::new));
            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
            beanDefinitionRegistry.registerBeanDefinition("antPatternDocketImpl" + index++, beanDefinition);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    private void initGroupNameMap() throws IOException, ReflectiveOperationException {
        groupNameMap = new HashMap<>();

        ClassPathScanningProvider classPathScanningProvider = new ClassPathScanningProvider();
        Set<Class<?>> classSet = classPathScanningProvider.findClassSet(clazz -> clazz.isAnnotationPresent(RestController.class), BASE_PACKAGES);
        for (Class<?> c : classSet) {
            String path = "";
            if (c.isAnnotationPresent(RequestMapping.class)) {
                path = getRequestMappingPathForString(c);
            }

            Method[] methods = c.getMethods();
            for (Method method : methods) {
                DocketGroup[] docketGroups = null;
                if (method.isAnnotationPresent(DocketGroup.class)) {
                    DocketGroup docketGroupName = method.getAnnotation(DocketGroup.class);
                    docketGroups = new DocketGroup[]{docketGroupName};
                } else if (method.isAnnotationPresent(DocketGroups.class)) {
                    docketGroups = method.getAnnotation(DocketGroups.class).value();
                } else {
                    continue;
                }

                if (ArrayUtils.isEmpty(docketGroups)) {
                    continue;
                }

                for (DocketGroup docketGroup : docketGroups) {
                    String groupName = docketGroup.name();
                    Map<String, String[]> pathMap = groupNameMap.get(groupName);
                    if (pathMap == null) {
                        pathMap = new HashMap<>();
                        groupNameMap.put(groupName, pathMap);
                    }
                    pathMap.put(path + getRequestMappingPathForString(method), docketGroup.tags());
                }
            }
        }
    }

    private String getRequestMappingPathForString(AnnotatedElement annotatedElement) throws ReflectiveOperationException {
        return StringUtils.trimToEmpty(Optional.ofNullable(getRequestMappingPathForArray(annotatedElement)).map(array -> array.length > 0 ? array[0] : null).orElse(null));
    }

    private String[] getRequestMappingPathForArray(AnnotatedElement annotatedElement) throws ReflectiveOperationException {
        Class<? extends Annotation>[] classes = new Class[]{RequestMapping.class,
                GetMapping.class,
                PostMapping.class,
                PutMapping.class,
                DeleteMapping.class};
        for (Class<? extends Annotation> c : classes) {
            if (annotatedElement.isAnnotationPresent(c)) {
                Annotation annotation = annotatedElement.getAnnotation(c);
                annotation.getClass().getMethods();
                return Optional
                        // value
                        .ofNullable((String[]) annotation.getClass().getMethod("value").invoke(annotation))
                        // path
                        .orElse((String[]) annotation.getClass().getMethod("path").invoke(annotation));
            }
        }
        return null;
    }

    /* <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 扩展 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */

    @Bean
    public Docket defaultDocket() {
        return new BasePackageDocketImpl(DEFAULT_GROUP_NAME, BASE_PACKAGES);
    }

    /**
     * 按antPattern进行分组
     */
    public static class AntPatternDocketImpl extends AbstractDocket {

        public AntPatternDocketImpl(String groupName, String... antPatterns) {
            /**
             * {@link PathSelectors#ant(String)}
             */
            Predicate<String> pathsSelector = input -> {
                AntPathMatcher matcher = new AntPathMatcher();
                for (String antPattern : antPatterns) {
                    if (matcher.match(antPattern, input)) {
                        return true;
                    }
                }
                return false;
            };
            setGroupName(groupName);
            setApisSelector(RequestHandlerSelectors.any());
            setPathsSelector(pathsSelector);
        }

    }

    /**
     * 根据basePackage进行分组
     */
    public static class BasePackageDocketImpl extends AbstractDocket {

        public BasePackageDocketImpl(String groupName, String... basePackages) {
            /**
             * see {@link RequestHandlerSelectors#basePackage(String)}
             */
            Function<Class<?>, Boolean> handlerPackage = input ->
                    Arrays.stream(basePackages)
                            .filter(basePackage -> ClassUtils.getPackageName(input).startsWith(basePackage))
                            .findFirst()
                            .map(basePackage -> true)
                            .orElse(false);
            Predicate<RequestHandler> apisSelector = requestHandler ->
                    Optional.ofNullable(requestHandler.declaringClass())
                            .map(handlerPackage)
                            .orElse(true);
            setGroupName(groupName);
            setApisSelector(apisSelector);
            setPathsSelector(PathSelectors.any());
        }

    }

    public static abstract class AbstractDocket extends Docket {

        @Value("${spring.application.name}")
        private String name;

        @Value("${server.port}")
        private int port;

        @Value("${spring.application.version}")
        private String version;

        @Setter
        private String groupName;

        @Setter
        private Predicate<RequestHandler> apisSelector;

        @Setter
        private Predicate<String> pathsSelector;

        public AbstractDocket() {
            this(DocumentationType.SWAGGER_2);
        }

        public AbstractDocket(DocumentationType documentationType) {
            super(documentationType);
        }

        @PostConstruct
        public void init() {
            useDefaultResponseMessages(false);

            // ApiInfo
            ApiInfo apiInfo = new ApiInfoBuilder()
                    // 设置文档标题
                    .title(TITLE)
                    // 文档描述
                    .description(DESCRIPTION)
                    // 服务条款URL
                    .termsOfServiceUrl(String.format("http://localhost:%d/", port))
                    // 版本号
                    .version(String.format("v%s", version))
                    // 联系
                    .contact(new Contact("xiangqian", "https://github.com/xiangqians/monolithic", "xiangqian@xiangqians.org"))
                    .build();
            apiInfo(apiInfo);

            enable(true);
            select()
                    .apis(apisSelector)
                    .paths(pathsSelector)
                    .build();

            // 组名称
            groupName(groupName);
        }

    }

}
