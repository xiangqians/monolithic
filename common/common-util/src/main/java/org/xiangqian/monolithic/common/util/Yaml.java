package org.xiangqian.monolithic.common.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

/**
 * @author xiangqian
 * @date 20:32 2023/05/19
 */
public class Yaml {

    private Map<String, String> map;

    public Yaml(String content) {
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        this.map = yaml.loadAs(content, Map.class);
    }

    public Yaml(InputStream inputStream) {
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        this.map = yaml.loadAs(inputStream, Map.class);
    }

    public Integer getInt(String name) {
        String value = getString(name);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public String getString(String name) {
        Object objValue = get(name);
        if (objValue != null) {
            String strValue = objValue.toString().trim();

            // eg: ${REGISTER_HOST:register}
            if (strValue.startsWith("${") && strValue.contains(":") && strValue.endsWith("}")) {
                int index = strValue.indexOf(":");
                name = strValue.substring("${".length(), index);
                return Optional.ofNullable(System.getenv(name)).orElse(strValue.substring(index + 1, strValue.length() - "}".length()));
            }

            return strValue;
        }

        return null;
    }

    public List getList(String name) {
        Object value = get(name);
        if (value instanceof List) {
            return (List) value;
        }
        return null;
    }

    private Object get(String name) {
        Queue<String> names = new LinkedList<>();
        String regex = "(.+)\\[(\\d+)\\]$";
        String[] array = name.split("\\.");
        for (String str : array) {
            String[] strs = RegexUtil.extractValues(regex, str);
            if (ArrayUtils.isNotEmpty(strs)) {
                Arrays.stream(strs).forEach(names::add);
            } else {
                names.add(str);
            }
        }
        return get(map, names);
    }

    private Object get(Object object, Queue<String> names) {
        if (names.isEmpty()) {
            return object;
        }

        // 从队列中获取并移除队头（即最先添加的元素），如果队列为空则返回null
        String name = names.poll();

        // name1.name2.name3
        if (object instanceof Map) {
            Map<String, String> map = (Map<String, String>) object;
            if (MapUtils.isEmpty(map)) {
                return null;
            }

            Object value = map.get(name);
            return get(value, names);
        }

        // name1.name2.name4[2]
        if (object instanceof List) {
            try {
                int index = Integer.parseInt(name);
                List list = (List) object;
                return list.get(index);
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static <T> T loadAs(InputStream inputStream, Class<T> type) throws IOException {
        if (List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            return new org.yaml.snakeyaml.Yaml().loadAs(inputStream, type);
        }

        Constructor constructor = new Constructor(new LoaderOptions());
        constructor.setPropertyUtils(new PropertyUtils() {
            @Override
            public Property getProperty(Class<? extends Object> type, String name) {
                // 忽略 yaml 中无法在类中找到属性的字段
                setSkipMissingProperties(true);

                Property property = super.getProperty(type, name);
                if (property instanceof MissingProperty) {
                    property = super.getProperty(type, NamingUtil.lowerHyphenConvertToLowerCamel(name));
                }
                return property;
            }
        });

        // 字符串转为 Duration
        constructor.addTypeDescription(new TypeDescription(Duration.class) {
            @Override
            public Object newInstance(Node node) {
                ScalarNode scalarNode = (ScalarNode) node;
                String value = scalarNode.getValue();
                return DurationUtil.parse(value);
            }
        });

        return new org.yaml.snakeyaml.Yaml(constructor).loadAs(inputStream, type);
    }

}
