package org.xiangqian.monolithic.util;

import org.apache.commons.collections4.MapUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author xiangqian
 * @date 20:32 2023/05/19
 */
public class YamlUtil {

    public static Map<String, Object> load(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.loadAs(new FileInputStream(file), Map.class);
        return new YamlMap(map);
    }

    private static class YamlMap implements Map<String, Object> {
        private Map<String, Object> source;

        public YamlMap(Map<String, Object> source) {
            this.source = source;
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(Object key) {
            if (Objects.isNull(key)) {
                return null;
            }

            Queue<String> keyQueue = new LinkedList<>();
            Arrays.stream(key.toString().split("\\.")).forEach(keyQueue::add);
            return get(source, keyQueue);
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

        private static Object get(Object source, Queue<String> keyQueue) {
            if (keyQueue.isEmpty()) {
                return source;
            }

            // poll
            String key = keyQueue.poll();

            // key1.key2.key3
            if (source instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) source;
                if (MapUtils.isEmpty(map)) {
                    return null;
                }

                Object value = map.get(key);
                return get(value, keyQueue);
            }

            // key1.key2.key4[2]
            if (source instanceof Collection) {
                return null;
            }

            return null;
        }
    }

}
