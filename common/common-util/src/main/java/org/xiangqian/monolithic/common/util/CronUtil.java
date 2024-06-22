package org.xiangqian.monolithic.common.util;

import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Java Spring CRON
 * <p>
 * Java Spring CRON 表达式由6个字段组成，以空格分隔：
 * ┌─────────────────────── 秒   (0 - 59)，表示每分钟的第几秒执行任务
 * │ ┌───────────────────── 分钟 (0 - 59)，表示每小时的第几分钟执行任务
 * │ │ ┌─────────────────── 小时 (0 - 23)，表示每天的第几个小时执行任务
 * │ │ │ ┌───────────────── 日   (1 - 31)，表示每月的第几天执行任务
 * │ │ │ │ ┌─────────────── 月份 (1 - 12 或 JAN-DEC：jan,feb,mar,apr ...)，表示每年的哪个月份执行任务
 * │ │ │ │ │ ┌───────────── 星期 (0 - 7 或 SUN-SAT：sun,mon,tue,wed,thu,fri,sat，0 和 7 都代表星期日)，表示每周的哪一天执行任务
 * │ │ │ │ │ │
 * │ │ │ │ │ │
 * * * * * * *
 * <p>
 * 特殊字符：
 * * 表示所有可能的值。例如在分钟字段中使用 * 表示每分钟都执行。
 * / 表示递增步长。例如在分钟字段中使用 0/5 表示从第0分钟开始，每隔5分钟执行一次。
 * - 表示范围。例如在小时字段中使用 10-12 表示10点、11点、12点这三个小时。
 * , 表示列表。例如在星期字段中使用 1,3,5 表示星期一、星期三和星期五。
 * L 表示“最后”。在日期字段中使用可以表示该月的最后一天，在星期字段中使用可以表示该月的最后一个星期几。例如5L表示该月的最后一个星期五。
 * W 表示工作日（周一至周五），可以和日期一起使用。例如20W表示离每月20号最近的工作日。
 * ? 表示不指定值，通常用于“日期”和“星期”字段相互排斥的场景中使用。例如，如果你希望在每月的第五天执行，但无关乎这是星期几，可以在星期字段中使用 ?。
 *
 * @author xiangqian
 * @date 23:47 2024/06/20
 */
public class CronUtil {

    // 常见示例：
    // `* * * * * ?` 每秒钟执行一次 |
    // `0 30 * * * ?` 每小时的第30分钟、第0秒钟执行一次 |
    // `0 0 1 * * ?` 每天的凌晨1点、第0分钟、第0秒钟执行一次 |
    // `0 0 2 ? * SUN` 每周日的凌晨2点、第0分钟、第0秒钟执行一次 |
    // `0 0 3 1 * ?` 每月1号的凌晨3点、第0分钟、第0秒钟执行一次 |
    // `0 0 4 1 1 ?` 每年的1月1日、凌晨4点、第0分钟、第0秒钟执行一次 |
    // `0 30 9 ? * MON-FRI` 每个工作日（周一到周五）的早上9点30分0秒钟执行一次 |
    // `0 */5 * * * ?` 每隔5分钟0秒钟执行一次，分钟为0和5的倍数 |
    // `0 15,45 * * * ?` 每小时的第15分钟0秒钟和第45分钟0秒钟各执行一次 |
    // `0 0 0 L * ?` 每个月的最后一天的午夜（即月底的0点钟0分钟0秒钟）执行一次 |
    // `0 0 16 ? * 5#1` 每月的第一个星期五的下午4点0分钟0秒钟执行一次 |

    /**
     * 获取接下来N次执行时间集合
     *
     * @param expression
     * @param n
     * @return
     */
    public static List<LocalDateTime> getNextNExecutionTimes(String expression, int n) {
        CronTrigger cronTrigger = new CronTrigger(expression);
        SimpleTriggerContext triggerContext = new SimpleTriggerContext();
        List<LocalDateTime> nextNExecutionTimes = new ArrayList<>(n);
        while (n-- > 0) {
            Instant nextExecutionTime = cronTrigger.nextExecution(triggerContext);
            triggerContext.update(nextExecutionTime, nextExecutionTime, nextExecutionTime);
            nextNExecutionTimes.add(LocalDateTime.ofInstant(nextExecutionTime, ZoneIdUtil.DEFAULT));
        }
        return nextNExecutionTimes;
    }

    public static String toString(LocalDateTime nextExecutionTime) {
        return String.format("%s 当前月的第%s周 %s",
                DateTimeUtil.format(nextExecutionTime),
                nextExecutionTime.toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfMonth()),
                nextExecutionTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINA));
    }

    public static String toString(List<LocalDateTime> nextNExecutionTimes) {
        StringBuilder builder = new StringBuilder();
        for (LocalDateTime nextExecutionTime : nextNExecutionTimes) {
            if (!builder.isEmpty()) {
                builder.append('\n');
            }
            builder.append(toString(nextExecutionTime));
        }
        return builder.toString();
    }

}
