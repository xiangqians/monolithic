package org.xiangqian.monolithic.biz;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * 资源工具
 *
 * @author xiangqian
 * @date 21:05 2022/09/07
 */
public class ResourceUtil {

    private static final Provider provider = new Provider();

    /**
     * 获取包集
     *
     * @param basePkgs 基础包集
     * @return
     * @throws IOException
     */
    public static Set<String> getPkgs(String... basePkgs) throws IOException {
        Assert.isTrue(ArrayUtils.isNotEmpty(basePkgs), "basePkgs must not be null");
        return get(capacity -> new HashSet<>(capacity, 1f), resource -> classResolver.apply(resource).getPackageName(), resolveBasePkgs(basePkgs));
    }

    /**
     * 获取类集
     *
     * @param basePkgs 基础包集
     * @return
     * @throws IOException
     */
    public static Set<Class> getClasses(String... basePkgs) throws IOException {
        Assert.isTrue(ArrayUtils.isNotEmpty(basePkgs), "basePkgs must not be null");
        return get(capacity -> new HashSet<>(capacity, 1f), classResolver, resolveBasePkgs(basePkgs));
    }

    private static final Function<Resource, Class> classResolver = resource -> {
        try {
            MetadataReader metadataReader = provider.getMetadataReader(resource);
            String className = metadataReader.getAnnotationMetadata().getClassName();
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private static String[] resolveBasePkgs(String... basePkgs) {
        for (int i = 0, length = basePkgs.length; i < length; i++) {
            String basePkg = basePkgs[i];
            basePkgs[i] = basePkg.replace('.', '/') + "/*.class";
        }
        return basePkgs;
    }

    /**
     * 获取资源集
     *
     * @param locationPatterns 位置匹配集合
     * @return
     * @throws IOException
     */
    public static Set<Resource> getResources(String... locationPatterns) throws IOException {
        Assert.isTrue(ArrayUtils.isNotEmpty(locationPatterns), "locationPatterns must not be null");
        return get(capacity -> new HashSet<>(capacity, 1f), Function.identity(), locationPatterns);
    }

    /**
     * 获取资源集
     *
     * @param supplier         <C> 提供者
     * @param resolver         {@link Resource} 解析器
     * @param locationPatterns 位置匹配集合
     * @param <T>
     * @param <C>
     * @return
     * @throws IOException
     */
    private static <T, C extends Collection<T>> C get(Function<Integer, C> supplier, Function<Resource, T> resolver, String... locationPatterns) throws IOException {
        Assert.notNull(supplier, "supplier must not be null");
        Assert.notNull(resolver, "resolver must not be null");
        Assert.isTrue(ArrayUtils.isNotEmpty(locationPatterns), "locationPatterns must not be null");

        int length = locationPatterns.length;
        Resource[][] resourcess = new Resource[length][];
        for (int i = 0; i < length; i++) {
            String locationPattern = "classpath*:" + locationPatterns[i];
            Resource[] resources = provider.getResources(locationPattern);
            resourcess[i] = resources;
        }

        int capacity = 0;
        for (Resource[] resources : resourcess) {
            if (resources != null) {
                capacity += resources.length;
            }
        }
        C c = supplier.apply(capacity);

        for (Resource[] resources : resourcess) {
            if (resources != null) {
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        T t = resolver.apply(resource);
                        if (t != null) {
                            c.add(t);
                        }
                    }
                }
            }
        }

        return c;
    }

    /**
     * See {@link org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider}
     */
    private static class Provider implements EnvironmentCapable {
        @Getter
        private Environment environment;

        private ResourcePatternResolver resourcePatternResolver;

        private MetadataReaderFactory metadataReaderFactory;

        Provider() {
            this.environment = new StandardEnvironment();
            this.setResourceLoader(null);
        }

        public void setResourceLoader(ResourceLoader resourceLoader) {
            if (resourceLoader instanceof ResourcePatternResolver) {
                resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
            } else {
                resourcePatternResolver = resourceLoader != null ? new PathMatchingResourcePatternResolver(resourceLoader) : new PathMatchingResourcePatternResolver();
            }
            metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
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
    }

}
