package org.monolithic.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * 扫描指定的包
 * <p>
 * {@link org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider}
 * 基于spring框架扫描包工具类下自定义扫描包
 *
 * @author xiangqian
 * @date 21:05 2022/09/07
 */
@Slf4j
public class ClassPathScanningProvider implements EnvironmentCapable, ResourceLoaderAware {

    private String resourcePattern;

    @Nullable
    private Environment environment;

    @Nullable
    private ResourcePatternResolver resourcePatternResolver;

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;

    public ClassPathScanningProvider() {
        this.resourcePattern = "**/*.class";
        this.environment = new StandardEnvironment();
        this.setResourceLoader(null);
    }

    public final Environment getEnvironment() {
        if (environment == null) {
            environment = new StandardEnvironment();
        }
        return environment;
    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        if (resourceLoader instanceof ResourcePatternResolver) {
            resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
        } else {
            resourcePatternResolver = resourceLoader != null ? new PathMatchingResourcePatternResolver(resourceLoader) : new PathMatchingResourcePatternResolver();
        }

        metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    public Set<Class<?>> findClassSet(Function<Class<?>, Boolean> candidateFunction, String... basePackages) throws IOException {
        return find(className -> {
            try {
                Class<?> c = Class.forName(className);
                if (BooleanUtils.isTrue(candidateFunction.apply(c))) {
                    return c;
                }
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }
            return null;
        }, basePackages);
    }

    /**
     * find
     *
     * @param candidateFunction 候选组件
     * @param basePackages      基础包名
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> Set<T> find(Function<String, T> candidateFunction, String... basePackages) throws IOException {
        Set<T> set = new HashSet<>();
        for (String basePackage : basePackages) {
            String packageSearchPath = "classpath*:" + resolveBasePackage(basePackage) + '/' + resourcePattern;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            int len = resources.length;
            for (int i = 0; i < len; ++i) {
                Resource resource = resources[i];
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getAnnotationMetadata().getClassName();
                    if (Objects.nonNull(candidateFunction)) {
                        Optional.ofNullable(candidateFunction.apply(className)).ifPresent(t -> set.add(t));
                    }
                }
            }
        }
        return set;
    }

    private String resolveBasePackage(String basePackage) {
        String className = getEnvironment().resolveRequiredPlaceholders(basePackage);
        Assert.notNull(className, "Class name must not be null");
        return className.replace('.', '/');
    }

}
