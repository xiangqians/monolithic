package org.xiangqian.monolithic.biz;

import java.time.format.DateTimeFormatter;

/**
 * @author xiangqian
 * @date 23:05 2024/05/31
 */
public class TimeUtil {

    public static final String PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

}
