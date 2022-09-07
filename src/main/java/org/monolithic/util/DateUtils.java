package org.monolithic.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author xiangqian
 * @date 22:56 2022/08/16
 */
public class DateUtils {

    public static final String LOCAL_DATE_TIME_DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter LOCAL_DATE_TIME_DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_DEFAULT_PATTERN);

    public static final String LOCAL_DATE_DEFAULT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter LOCAL_DATE_DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_DEFAULT_PATTERN);

    public static LocalDateTime parseForLocalDateTime(String text) {
        return parseForLocalDateTime(text, LOCAL_DATE_TIME_DEFAULT_PATTERN);
    }

    public static LocalDateTime parseForLocalDateTime(String text, String pattern) {
        return parseForLocalDateTime(text, LOCAL_DATE_TIME_DEFAULT_PATTERN.equals(pattern) ? LOCAL_DATE_TIME_DEFAULT_FORMATTER : DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseForLocalDateTime(String text, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(text, dateTimeFormatter);
    }

    public static LocalDate parseForLocalDate(String text) {
        return parseForLocalDate(text, LOCAL_DATE_DEFAULT_PATTERN);
    }

    public static LocalDate parseForLocalDate(String text, String pattern) {
        return LocalDate.parse(text, LOCAL_DATE_DEFAULT_PATTERN.equals(pattern) ? LOCAL_DATE_DEFAULT_FORMATTER : DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseForLocalDate(String text, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(text, dateTimeFormatter);
    }

    public static String format(LocalDateTime date) {
        return format(date, LOCAL_DATE_TIME_DEFAULT_PATTERN);
    }

    public static String format(LocalDateTime date, String pattern) {
        return format(date, LOCAL_DATE_TIME_DEFAULT_PATTERN.equals(pattern) ? LOCAL_DATE_TIME_DEFAULT_FORMATTER : DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime date, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(date);
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long dateToTimestamp(LocalDateTime date) {
        ZoneId zone = ZoneId.systemDefault();
//        return date.atZone(zone).toEpochSecond(); // s
        return date.atZone(zone).toInstant().toEpochMilli(); // ms
    }

    public static LocalDate timestampToLocalDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long dateToTimestamp(LocalDate date) {
//        return date.toEpochDay(); // day
//        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(); // s
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(); // ms
    }

}
