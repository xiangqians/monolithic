package org.xiangqian.monolithic.common.util.time;

import org.junit.Test;

import java.time.LocalTime;

/**
 * @author xiangqian
 * @date 19:34 2023/06/28
 */
public class TimeUtilTest {

    @Test
    public void testFormat() {
        LocalTime time = LocalTime.now();
        System.out.println(time);

        String format = TimeUtil.format(time);
        System.out.println(format);

        time = TimeUtil.parse(format);
        System.out.println(time);
    }

}
