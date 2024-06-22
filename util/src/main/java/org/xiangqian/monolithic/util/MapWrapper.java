package org.xiangqian.monolithic.util;

import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 23:38 2024/06/18
 */
public class MapWrapper {

    private Map map;

    public MapWrapper(Map map) {
        this.map = map;
    }

    public Integer getInt(Object key, Integer defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Integer getInt(Object key) {
        return getInt(key, null);
    }

    public Long getLong(Object key, Long defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Long getLong(Object key) {
        return getLong(key, null);
    }

    public String getString(Object key, String defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public String getString(Object key) {
        return getString(key, null);
    }

    public Map getMap(Object key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map) value;
        }
        return null;
    }

    public List getList(Object key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List) value;
        }
        return null;
    }

    public Object getObject(Object key) {
        return map.get(key);
    }

}
