package org.xiangqian.monolithic.common.util;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 19:58 2024/02/27
 */
public class DurationUtil {

    /**
     * 在 Java 中，可以使用 Duration.parse() 方法将文本表示的持续时间解析为 Duration 对象。
     * 但是要注意，Duration.parse() 方法需要遵循 ISO-8601 格式的时间表示形式。
     * 对于文本 "10s"，它表示一个持续时间为 10 秒的时间段，可以按照 ISO-8601 格式进行表示。
     * 在 ISO-8601 格式中，表示持续时间的格式为 "PTnS"，其中 "n" 为持续时间的数字部分。
     * 因此，可以将文本 "10s" 转换为 ISO-8601 格式字符串 "PT10S"，然后使用 Duration.parse() 方法进行解析。
     *
     * @param text
     * @return
     */
    public static Duration parse(String text) {
        return Duration.parse("PT" + text.trim().toLowerCase()
                .replace("h", "H")
                .replace("m", "M")
                .replace("s", "S"));
    }

}
