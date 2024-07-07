package org.xiangqian.monolithic.common.util.time;

import org.junit.Test;

import java.time.LocalDate;

/**
 * @author xiangqian
 * @date 19:34 2023/06/28
 */
public class DateUtilTest {

    @Test
    public void testFormat() {
        LocalDate date = LocalDate.now();
        System.out.println(date);

        String format = DateUtil.format(date);
        System.out.println(format);

        date = DateUtil.parse(format);
        System.out.println(date);
    }


}
