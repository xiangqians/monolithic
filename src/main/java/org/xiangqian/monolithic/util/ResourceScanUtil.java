package org.xiangqian.monolithic.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * 资源扫描工具
 * <p>
 * 基于spring框架扫描包工具类下自定义扫描资源
 * See {@link org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider}
 *
 * @author xiangqian
 * @date 21:05 2022/09/07
 */
@Slf4j
public class ResourceScanUtil {

    private static final Provider provider = new Provider();

    private static final Function<Resource, Class<?>> classResolver = resource -> {
        try {
            MetadataReader metadataReader = provider.getMetadataReader(resource);
            String className = metadataReader.getAnnotationMetadata().getClassName();
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private static final Function<Resource, File> fileResolver = resource -> {
        throw new UnsupportedOperationException();
    };

    private static final Function<String, String> locationPatternResolver = basePkg -> {
        String className = provider.getEnvironment().resolveRequiredPlaceholders(basePkg);
        Assert.notNull(className, "Class name must not be null");
        return String.format("classpath*:%s/**/*.class", className.replace('.', '/'));
    };

    private static final Function<String[], String[]> locationPatternsResolver = basePkgs -> Arrays.stream(basePkgs).map(locationPatternResolver).toArray(String[]::new);

    public static Set<Class<?>> scanClasses(String... basePkgs) throws IOException {
        return scanClasses(c -> true, basePkgs);
    }

    /**
     * 扫描 Class
     *
     * @param candidate
     * @param basePkgs
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> scanClasses(Function<Class<?>, Boolean> candidate, String... basePkgs) throws IOException {
        Assert.notNull(candidate, "candidate must not be null");
        Assert.isTrue(ArrayUtils.isNotEmpty(basePkgs), "basePkgs must not be null");
        return scan(resource -> {
            Class<?> c = classResolver.apply(resource);
            return BooleanUtils.isTrue(candidate.apply(c)) ? c : null;
        }, locationPatternsResolver.apply(basePkgs));
    }

    public static Set<String> scanPkgs(String... basePkgs) throws IOException {
        Assert.isTrue(ArrayUtils.isNotEmpty(basePkgs), "basePkgs must not be null");
        return scan(resource -> classResolver.apply(resource).getPackageName(), locationPatternsResolver.apply(basePkgs));
    }

    /**
     * 扫描
     *
     * @param resolver         {@link Resource} 解析器
     * @param locationPatterns 位置匹配集
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> Set<T> scan(Function<Resource, T> resolver, String... locationPatterns) throws IOException {
        Assert.notNull(resolver, "resolver must not be null");
        Assert.isTrue(ArrayUtils.isNotEmpty(locationPatterns), "locationPatterns must not be null");
        Set<T> set = new HashSet<>(16, 1f);
        for (String locationPattern : locationPatterns) {
            Resource[] resources = provider.getResources(locationPattern);
            for (int i = 0, len = resources.length; i < len; i++) {
                Resource resource = resources[i];
                if (resource.isReadable()) {
                    Optional.ofNullable(resolver.apply(resource)).ifPresent(set::add);
                }
            }
        }
        return set;
    }

    static class Provider implements EnvironmentCapable, ResourceLoaderAware {

        @Getter
        @Nullable
        private Environment environment;

        @Nullable
        private ResourcePatternResolver resourcePatternResolver;

        @Nullable
        private MetadataReaderFactory metadataReaderFactory;

        Provider() {
            this.environment = new StandardEnvironment();
            this.setResourceLoader(null);
        }

        public MetadataReader getMetadataReader(String className) throws IOException {
            return metadataReaderFactory.getMetadataReader(className);
        }

        public MetadataReader getMetadataReader(Resource resource) throws IOException {
            return metadataReaderFactory.getMetadataReader(resource);
        }

        public Resource[] getResources(String locationPattern) throws IOException {
            return resourcePatternResolver.getResources(locationPattern);
        }

        public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
            if (resourceLoader instanceof ResourcePatternResolver) {
                resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
            } else {
                resourcePatternResolver = Objects.nonNull(resourceLoader) ? new PathMatchingResourcePatternResolver(resourceLoader) : new PathMatchingResourcePatternResolver();
            }
            metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }

    }

}
