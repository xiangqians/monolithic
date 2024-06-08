package org.xiangqian.monolithic.util;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

/**
 * @author xiangqian
 * @date 20:02 2024/06/07
 */
public class ResourceUtilTest {

    @Test
    public void testgetResources2() throws IOException {
        Set<Resource> resources = ResourceUtil.getResources("**/*.properties");
        System.out.println(resources.size());
        for (Resource resource : resources) {
            System.out.println(resource);
            System.out.println(StringUtils.join(IOUtils.readLines(resource.getInputStream(), CharEncoding.UTF_8), "\n"));
            System.out.println();
        }
    }

    @Test
    public void testgetResources1() throws IOException {
        Set<Resource> resources = ResourceUtil.getResources("org/xiangqian/monolithic/*/*.class");
        System.out.println(resources.size());
        resources.forEach(System.out::println);
    }

    @Test
    public void testGetPkgs() throws IOException {
        Set<String> pkgs = ResourceUtil.getPkgs("org.xiangqian.monolithic.*");
        System.out.println(pkgs.size());
        pkgs.forEach(System.out::println);
        System.out.println();

        pkgs = ResourceUtil.getPkgs("org.apache.commons.collections4.trie");
        System.out.println(pkgs.size());
        pkgs.forEach(System.out::println);
        System.out.println();

        pkgs = ResourceUtil.getPkgs("org.apache.commons.collections4.trie.**");
        System.out.println(pkgs.size());
        pkgs.forEach(System.out::println);
        System.out.println();
    }

    @Test
    public void testGetClasses() throws IOException {
        Set<Class<?>> classes = ResourceUtil.getClasses("org.xiangqian.monolithic.*");
        System.out.println(classes.size());
        classes.forEach(System.out::println);
        System.out.println();

        classes = ResourceUtil.getClasses("org.apache.commons.collections4.trie");
        System.out.println(classes.size());
        classes.forEach(System.out::println);
        System.out.println();

        classes = ResourceUtil.getClasses("org.apache.commons.collections4.trie.**");
        System.out.println(classes.size());
        classes.forEach(System.out::println);
        System.out.println();
    }

}
