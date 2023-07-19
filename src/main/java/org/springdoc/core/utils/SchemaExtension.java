package org.springdoc.core.utils;

import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 20:27 2023/05/11
 */
@Slf4j
public class SchemaExtension {

    public static Map<String, Schema> extend(Type type, Map<String, Schema> schemaMap) throws Exception {
        // Class ?
        if (!(type instanceof Class)) {
            return schemaMap;
        }

        // c
        Class<?> c = (Class<?>) type;

        // class info
        ClassInfo classInfo = ClassInfo.get(c);
        if (Objects.isNull(classInfo)) {
            return schemaMap;
        }

        // Schema
        Schema schema = schemaMap.get(classInfo.getName());
        if (Objects.isNull(schema)) {
            return schemaMap;
        }

        // <group, []FieldInfo> map
        Map<Class<?>, Set<FieldInfo>> gfsMap = classInfo.getGfsMap();
        if (MapUtils.isEmpty(gfsMap)) {
            return schemaMap;
        }

        Map<String, Schema> newSchemaMap = new LinkedHashMap<>(schemaMap.size() + gfsMap.size(), 1f);
        newSchemaMap.putAll(schemaMap);

        for (Map.Entry<Class<?>, Set<FieldInfo>> entry : gfsMap.entrySet()) {
            // group
            Class<?> group = entry.getKey();
            // field infos
            Set<FieldInfo> fieldInfos = entry.getValue();

            // new schema
            Schema newSchema = createSchema(schema, group, fieldInfos);

            // put
            newSchemaMap.put(newSchema.getName(), newSchema);
        }

        log.info("extend {} {}", c.getName(), gfsMap.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList()));

        return newSchemaMap;
    }

    private static Schema createSchema(Schema schema, Class<?> group, Set<FieldInfo> fieldInfos) {
        // new schema
        Schema newSchema = new Schema();
        BeanUtils.copyProperties(schema, newSchema);

        // set name
        newSchema.setName(group.getSimpleName() + newSchema.getName());

        // required
        List<String> required = new ArrayList<>(newSchema.getRequired().size());

        // properties
        Map<String, Schema> origProperties = schema.getProperties(); // original properties
        Map<String, Schema> properties = new HashMap<>(origProperties.size());

        // foreach
        for (FieldInfo fieldInfo : fieldInfos) {
            String name = fieldInfo.getName();
            if (fieldInfo.isRequired()) {
                required.add(name);
            }

            Schema property = origProperties.get(name);
            if (Objects.nonNull(property)) {
                properties.put(name, property);
            }
        }

        // set
        newSchema.setRequired(required);
        newSchema.setProperties(properties);

        return newSchema;
    }

}
