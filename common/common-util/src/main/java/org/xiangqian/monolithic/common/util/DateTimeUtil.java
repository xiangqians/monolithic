package org.xiangqian.monolithic.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link LocalDateTime} 本地日期时间：表示了一个没有时区信息的日期和时间，并且它的内部实现是基于本地时钟的，不涉及时区转换。因此，{@link LocalDateTime} 对象的创建和操作通常比较高效。
 * <p>
 * {@link ZonedDateTime} 时区日期时间：类表示了一个带有时区信息的日期和时间。它提供了更多的功能，例如时区转换、跨时区计算等。由于其涉及到时区的处理，创建和操作 {@link ZonedDateTime} 对象可能会比 {@link LocalDateTime} 稍微慢一些。然而，这个性能差异通常是微小的，对于大多数情况来说不会对性能产生显著影响。
 * <p>
 * 日期时间格式
 * "yyyy"：年份
 * "MM"  ：月份
 * "dd"  ：日期
 * "HH"  ：小时（24小时制）
 * "mm"  ：分钟
 * "ss"  ：秒钟
 * "SSS" ：毫秒
 * 例如：
 * "yyyy/MM/dd"：年份、月份和日期（例如：2023/11/13）
 * "HH:mm:ss"  ：小时、分钟和秒钟（例如：10:31:38）
 * "yyyy/MM/dd HH:mm:ss.SSS"：完整的日期时间（例如：2023/11/13 10:31:38.491）
 *
 * @author xiangqian
 * @date 21:24 2022/08/01
 */
public class DateTimeUtil {

    public static final String PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static String format(LocalDateTime dateTime) {
        return FORMATTER.format(dateTime);
    }

    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, FORMATTER);
    }

    public static LocalDateTime ofInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneIdUtil.DEFAULT);
    }

    public static long toSecond(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneIdUtil.DEFAULT).toEpochSecond();
    }

    public static LocalDateTime ofSecond(long second) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(second), ZoneIdUtil.DEFAULT);
    }

    public static long toMillisecond(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneIdUtil.DEFAULT).toInstant().toEpochMilli();
    }

    public static LocalDateTime ofMillisecond(long millisecond) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneIdUtil.DEFAULT);
    }

    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneIdUtil.DEFAULT).toInstant());
    }

    public static LocalDateTime ofDate(Date date) {
        return date.toInstant().atZone(ZoneIdUtil.DEFAULT).toLocalDateTime();
    }

    public static LocalDateTime ofZonedDateTime(ZonedDateTime zonedDateTime) {
        // 时区转换
        zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneIdUtil.DEFAULT);

        return zonedDateTime.toLocalDateTime();
    }

    /**
     * 人性化日期时间戳（单位s）
     *
     * @param second
     * @return
     */
    public static String humanSecond(Long second) {
        if (second == null || second <= 0) {
            return "";
        }

        LocalDateTime dateTime = ofSecond(second);
        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        // 天
        if (duration.toDays() > 0) {
            return format(dateTime);
        }

        // 小时
        long hour = duration.toHours();
        if (hour > 0) {
            return hour + "小时前";
        }

        // 分钟
        long minute = duration.toMinutes();
        if (minute > 0) {
            return minute + "分钟前";
        }

        // 秒
        second = duration.toSeconds();
        return second + "秒前";
    }

    /**
     * 人性化间隔时间
     *
     * @param second
     * @return
     */
    public static String humanDurationSecond(Long second) {
        if (second == null) {
            return "";
        }

        if (second <= 0) {
            return second + "秒";
        }

        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);

        // 小时
        long hour = duration.toHours();
        if (hour > 0) {
            builder.append(hour).append("小时");
        }

        // 分钟
        int minute = duration.toMinutesPart();
        if (minute > 0) {
            builder.append(minute).append("分钟");
        }

        // 秒
        int second0 = duration.toSecondsPart();
        if (second0 > 0) {
            builder.append(second0).append("秒");
        }

        return builder.toString();
    }

}
