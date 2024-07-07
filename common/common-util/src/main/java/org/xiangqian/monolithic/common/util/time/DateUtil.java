package org.xiangqian.monolithic.common.util.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author xiangqian
 * @date 23:04 2024/05/31
 */
public class DateUtil {

    public static final String PATTERN = "yyyy/MM/dd";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static String format(LocalDate date) {
        return FORMATTER.format(date);
    }

    public static LocalDate parse(String text) {
        return LocalDate.parse(text, FORMATTER);
    }

    public static long toSecond(LocalDate date) {
        return date.atStartOfDay(ZoneIdUtil.DEFAULT).toEpochSecond();
    }

    public static LocalDate ofSecond(long second) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(second), ZoneIdUtil.DEFAULT);
    }

    public static long toMillisecond(LocalDate date) {
        return date.atStartOfDay(ZoneIdUtil.DEFAULT).toInstant().toEpochMilli();
    }

    public static LocalDate ofMillisecond(long millisecond) {
        return LocalDate.ofInstant(Instant.ofEpochMilli(millisecond), ZoneIdUtil.DEFAULT);
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneIdUtil.DEFAULT).toInstant());
    }

    public static LocalDate ofDate(Date date) {
        return date.toInstant().atZone(ZoneIdUtil.DEFAULT).toLocalDate();
    }

}
