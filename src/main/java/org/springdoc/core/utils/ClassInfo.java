package org.springdoc.core.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xiangqian
 * @date 20:02 2023/05/11
 */
@Getter
@Setter
public class ClassInfo {

    // class
    private Class<?> c;

    // schema
    private io.swagger.v3.oas.annotations.media.Schema schema;

    // name
    private String name;

    // <group, []FieldInfo> map
    private Map<Class<?>, Set<FieldInfo>> gfsMap;

    public static ClassInfo get(Class<?> c) throws Exception {
        // 实体类需要有 io.swagger.v3.oas.annotations.media.Schema 注解标识
        if (!c.isAnnotationPresent(io.swagger.v3.oas.annotations.media.Schema.class)) {
            return null;
        }

        // info
        ClassInfo info = new ClassInfo();

        // c
        info.setC(c);

        // schema
        io.swagger.v3.oas.annotations.media.Schema schema = c.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        info.setSchema(schema);

        // name
        String name = schema.name();
        if (StringUtils.isEmpty(name)) {
            name = c.getSimpleName();
        }
        info.setName(name);

        // group map
        info.setGfsMap(getGfsMap(c));

        return info;
    }

    private static Map<Class<?>, Set<FieldInfo>> getGfsMap(Class<?> c) throws Exception {
        // <group, []FieldInfo>
        Map<Class<?>, Set<FieldInfo>> gfsMap = new HashMap<>();
        for (Class<?> tc = c; tc != Object.class; tc = tc.getSuperclass()) {
            Field[] fields = tc.getDeclaredFields();
            if (ArrayUtils.isEmpty(fields)) {
                continue;
            }

            for (Field field : fields) {
                Set<FieldInfo> candidateFieldInfos = FieldInfo.gets(field);
                if (CollectionUtils.isEmpty(candidateFieldInfos)) {
                    continue;
                }

                for (FieldInfo candidateFieldInfo : candidateFieldInfos) {
                    Class<?> group = candidateFieldInfo.getGroup();
                    Set<FieldInfo> fieldInfos = gfsMap.get(group);
                    if (Objects.isNull(fieldInfos)) {
                        fieldInfos = new HashSet<>();
                        gfsMap.put(group, fieldInfos);
                    }
                    fieldInfos.add(candidateFieldInfo);
                }
            }
        }

        return gfsMap;
    }

}
