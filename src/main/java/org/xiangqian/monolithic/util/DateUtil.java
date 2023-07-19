package org.xiangqian.monolithic.util;

import org.apache.commons.collections4.map.LRUMap;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiangqian
 * @date 21:24 2022/08/01
 */
public class DateUtil {

    private static final Map<String, DateTimeFormatter> formatterMap;

    static {
        // 使用LRU算法（最近最少使用）
        formatterMap = new LRUMap<>(1024, 1f);
    }

    public synchronized static DateTimeFormatter getFormatter(String pattern) {
        DateTimeFormatter formatter = formatterMap.get(pattern);
        if (Objects.nonNull(formatter)) {
            return formatter;
        }

        formatter = DateTimeFormatter.ofPattern(pattern);
        formatterMap.put(pattern, formatter);
        return formatter;
    }

    /**
     * 格式化日期
     *
     * @param temporal {@link LocalDateTime} {@link LocalDate} {@link LocalTime}
     * @param pattern
     * @return
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return getFormatter(pattern).format(temporal);
    }

    /**
     * 解析日期文本
     *
     * @param text
     * @param pattern
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, String pattern, Class<T> type) {
        if (type == LocalDateTime.class) {
            return (T) LocalDateTime.parse(text, getFormatter(pattern));
        }

        if (type == LocalDate.class) {
            return (T) LocalDate.parse(text, getFormatter(pattern));
        }

        if (type == LocalTime.class) {
            return (T) LocalTime.parse(text, getFormatter(pattern));
        }

        throw new UnsupportedOperationException();
    }

    public static <T> T ofEpochMilli(long timestamp, Class<T> type) {
        return ofEpochMilli(timestamp, ZoneId.systemDefault(), type);
    }

    public static <T> T ofEpochMilli(long timestamp, String zoneId, Class<T> type) {
        return ofEpochMilli(timestamp, ZoneId.of(zoneId), type);
    }

    public static <T> T ofEpochMilli(long timestamp, ZoneId zoneId, Class<T> type) {
        if (type == LocalDate.class) {
            return (T) LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
        }

        if (type == LocalDateTime.class) {
            return (T) LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
        }

        throw new UnsupportedOperationException();
    }

    public static <T> T ofEpochSecond(long timestamp, Class<T> type) {
        return ofEpochSecond(timestamp, ZoneId.systemDefault(), type);
    }

    public static <T> T ofEpochSecond(long timestamp, String zoneId, Class<T> type) {
        return ofEpochSecond(timestamp, ZoneId.of(zoneId), type);
    }

    public static <T> T ofEpochSecond(long timestamp, ZoneId zoneId, Class<T> type) {
        if (type == LocalDate.class) {
            return (T) LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), zoneId);
        }

        if (type == LocalDateTime.class) {
            return (T) LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zoneId);
        }

        throw new UnsupportedOperationException();
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return toEpochMilli(dateTime, ZoneId.systemDefault());
    }

    public static long toEpochMilli(LocalDateTime dateTime, String zoneId) {
        return toEpochMilli(dateTime, ZoneId.of(zoneId));
    }

    public static long toEpochMilli(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long toEpochSecond(LocalDateTime dateTime) {
        return toEpochSecond(dateTime, ZoneId.systemDefault());
    }

    public static long toEpochSecond(LocalDateTime dateTime, String zoneId) {
        return toEpochSecond(dateTime, ZoneId.of(zoneId));
    }

    public static long toEpochSecond(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.atZone(zoneId).toEpochSecond();
    }

    public static long toEpochMilli(LocalDate date) {
        return toEpochMilli(date, ZoneId.systemDefault());
    }

    public static long toEpochMilli(LocalDate date, String zoneId) {
        return toEpochMilli(date, ZoneId.of(zoneId));
    }

    public static long toEpochMilli(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli();
    }

    public static long toEpochSecond(LocalDate date) {
        return toEpochSecond(date, ZoneId.systemDefault());
    }

    public static long toEpochSecond(LocalDate date, String zoneId) {
        return toEpochSecond(date, ZoneId.of(zoneId));
    }

    public static long toEpochSecond(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId).toEpochSecond();
    }

}
