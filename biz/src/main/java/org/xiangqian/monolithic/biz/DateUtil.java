package org.xiangqian.monolithic.biz;

import java.time.format.DateTimeFormatter;

/**
 * @author xiangqian
 * @date 23:04 2024/05/31
 */
public class DateUtil {

    public static final String PATTERN = "yyyy/MM/dd";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

}
