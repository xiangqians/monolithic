package org.xiangqian.monolithic.common.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xiangqian
 * @date 23:05 2024/05/31
 */
public class TimeUtil {

    public static final String PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static String format(LocalTime time) {
        return FORMATTER.format(time);
    }

    public static LocalTime parse(String text) {
        return LocalTime.parse(text, FORMATTER);
    }

}
