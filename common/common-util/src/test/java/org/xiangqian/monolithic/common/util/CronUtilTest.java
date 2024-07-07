package org.xiangqian.monolithic.common.util;

import org.junit.Test;

/**
 * @author xiangqian
 * @date 23:36 2024/06/20
 */
public class CronUtilTest {

    @Test
    public void test1() {
        // 每5秒钟执行一次
        String expression = "0/5 * * * * ?";
        CronUtil.printNextNExecutionTimes(expression, 10);
    }

    @Test
    public void test2() {
        String expression = "0 0 16 ? * 5#1";
        CronUtil.printNextNExecutionTimes(expression, 10);
    }

}
