package org.xiangqian.monolithic.common.util.time;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author xiangqian
 * @date 19:34 2023/06/28
 */
public class DateTimeUtilTest {

    @Test
    public void testFormat() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);

        String format = DateTimeUtil.format(dateTime);
        System.out.println(format);

        dateTime = DateTimeUtil.parse(format);
        System.out.println(dateTime);

        System.out.println(dateTime.atZone(ZoneIdUtil.AUSTRALIA_SYDNEY));
    }

    @Test
    public void testSecond() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);

        long second = DateTimeUtil.toSecond(dateTime);
        System.out.println(second);

        dateTime = DateTimeUtil.ofSecond(second);
        System.out.println(dateTime);
    }

    @Test
    public void testMillisecond() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);

        long millisecond = DateTimeUtil.toMillisecond(dateTime);
        System.out.println(millisecond);

        dateTime = DateTimeUtil.ofMillisecond(millisecond);
        System.out.println(dateTime);
    }

    @Test
    public void dateTimeToDate() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);

        Date date = DateTimeUtil.toDate(dateTime);
        System.out.println(date);

        dateTime = DateTimeUtil.ofDate(date);
        System.out.println(dateTime);
    }

}
