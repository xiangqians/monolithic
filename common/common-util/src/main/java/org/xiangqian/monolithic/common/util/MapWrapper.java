package org.xiangqian.monolithic.common.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 23:38 2024/06/18
 */
public class MapWrapper {

    private Map<?, ?> map;

    public MapWrapper(Map<?, ?> map) {
        this.map = map;
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public String getString(Object key, String defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    public String getString(Object key) {
        return getString(key, null);
    }

    public Integer getInt(Object key, Integer defaultValue) {
        Object value = get(key);
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

    public Float getFloat(String key, Float defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    public Long getLong(Object key, Long defaultValue) {
        Object value = get(key);
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

    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        return defaultValue;
    }

    public LocalDateTime getLocalDateTime(String key) {
        return getLocalDateTime(key, null);
    }

    public Date getDate(String key, Date defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        return defaultValue;
    }

    public Date getDate(String key) {
        return getDate(key, null);
    }

    public List<?> getList(Object key) {
        Object value = get(key);
        if (value instanceof List) {
            return (List) value;
        }
        return null;
    }

    public Map<?, ?> getMap(Object key) {
        Object value = get(key);
        if (value instanceof Map) {
            return (Map) value;
        }
        return null;
    }

    public <T> T deserialize(Class<T> type) {
        return JsonUtil.deserialize(map, type);
    }

    public <T> T deserialize(TypeReference<T> typeRef) {
        return JsonUtil.deserialize(map, typeRef);
    }

}
