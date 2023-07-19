package org.springdoc.core.utils;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.xiangqian.monolithic.validation.constraint.Nothing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xiangqian
 * @date 20:04 2023/05/11
 */
@Getter
@Setter
@AllArgsConstructor
public class FieldInfo {

    // field
    private Field field;

    // 字段swagger文档描述
    private io.swagger.v3.oas.annotations.media.Schema schema;

    // 字段名
    private String name;

    // 组
    private Class<?> group;

    // 是否必须
    private boolean required;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(group, fieldInfo.group) && Objects.equals(name, fieldInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name);
    }

    private static Class<?>[] getGroups(Field field, Class<? extends Annotation> type) throws Exception {
        if (!field.isAnnotationPresent(type)) {
            return null;
        }

        Annotation annotation = field.getAnnotation(type);
//            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
//            // memberValues field
//            Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
//            // jdk17 setAccessible err
//            memberValuesField.setAccessible(true);
//            // memberValues
//            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
//            Class<?>[] groups = (Class<?>[]) memberValues.get("groups");
//            return groups;

        Method method = annotation.getClass().getMethod("groups");
        return (Class<?>[]) method.invoke(annotation);
    }

    /**
     * 获取 {@link Field} <group（组）, types（声明组的注解类型集）> 映射
     *
     * @param field {@link Field}
     * @return <group（组）, types（声明组的注解类型集）>
     * @throws Exception
     */
    private static Map<Class<?>, Set<Class<? extends Annotation>>> getGroupTypesMap(Field field) throws Exception {
        Class<? extends Annotation>[] candidateTypes = new Class[]{
                Nothing.class,
                NotNull.class,
                Length.class,
        };

        // <group（组）, types（声明组的注解类型集）>
        Map<Class<?>, Set<Class<? extends Annotation>>> groupTypesMap = new HashMap<>();
        for (Class<? extends Annotation> candidateType : candidateTypes) {
            Class<?>[] groups = getGroups(field, candidateType);
            if (ArrayUtils.isEmpty(groups)) {
                continue;
            }

            for (Class<?> group : groups) {
                Set<Class<? extends Annotation>> types = groupTypesMap.get(group);
                if (Objects.isNull(types)) {
                    types = new HashSet<>();
                    groupTypesMap.put(group, types);
                }
                types.add(candidateType);
            }
        }

        return groupTypesMap;
    }

    public static Set<FieldInfo> gets(Field field) throws Exception {
        if (!field.isAnnotationPresent(io.swagger.v3.oas.annotations.media.Schema.class)) {
            return null;
        }

        // schema
        io.swagger.v3.oas.annotations.media.Schema schema = field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);

        // name
        String name = schema.name();
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }

        // required
        boolean required = schema.required();

        // <group（组）, types（声明组的注解类型集）>
        Map<Class<?>, Set<Class<? extends Annotation>>> groupTypesMap = getGroupTypesMap(field);
        if (MapUtils.isEmpty(groupTypesMap)) {
            return null;
        }

        // infos
        Set<FieldInfo> infos = new HashSet<>(groupTypesMap.size());
        for (Map.Entry<Class<?>, Set<Class<? extends Annotation>>> entry : groupTypesMap.entrySet()) {
            Class<?> group = entry.getKey();
            Set<Class<? extends Annotation>> types = entry.getValue();
            infos.add(new FieldInfo(field, schema, name, group, types.contains(NotNull.class) ? true : required));
        }

        return infos;
    }

}
