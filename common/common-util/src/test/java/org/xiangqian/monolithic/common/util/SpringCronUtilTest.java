package org.xiangqian.monolithic.common.util;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiangqian
 * @date 23:36 2024/06/20
 */
public class SpringCronUtilTest {

    public static void main(String[] args) {
        String expression = "0 0 16 ? * 5#1";
        List<LocalDateTime> nextNExecutionTimes = CronUtil.getNextNExecutionTimes(expression, 10);
        System.out.println(expression);
        System.out.println(CronUtil.toString(nextNExecutionTimes));
    }

}
