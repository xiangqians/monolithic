package org.xiangqian.monolithic.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 19:34 2023/06/28
 */
@Slf4j
public class DateTest {


    @Test
    public void test() {
        long s = DateUtil.toEpochSecond(LocalDate.now(), "GMT");
        System.out.println(s);

        System.out.println(DateUtil.toEpochSecond(LocalDate.now()));
        System.out.println(DateUtil.toEpochSecond(LocalDateTime.now()));
        System.out.println(DateUtil.toEpochMilli(LocalDate.now()));
        System.out.println(DateUtil.toEpochMilli(LocalDateTime.now()));

    }

    @Test
    public void test3() {
        LocalDateTime dateTime = LocalDateTime.now();
        long timestamp = DateUtil.toEpochSecond(dateTime);
        log.debug("{}", timestamp);

        ZoneId zoneId = ZoneId.of("Europe/London");
        timestamp = DateUtil.toEpochSecond(dateTime, zoneId);
        log.debug("{}", timestamp);
    }

    @Test
    public void test2() {
        String format = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd HH:mm:ss");
        log.debug("{}", format);

        ZoneId zoneId = ZoneId.of("Europe/London");
        LocalDateTime dateTime = LocalDateTime.now(zoneId);
        log.debug("{}", dateTime);

        format = DateUtil.format(dateTime, "yyyy/MM/dd HH:mm:ss");
        log.debug("{}", format);

        dateTime = DateUtil.parse(format, "yyyy/MM/dd HH:mm:ss", LocalDateTime.class);
        log.debug("{}", dateTime);

        long timestamp = DateUtil.toEpochSecond(dateTime);
        log.debug("{}", timestamp);

        dateTime = DateUtil.ofEpochSecond(timestamp, LocalDateTime.class);
        log.debug("{}", dateTime);
    }

    @Test
    public void test1() {
        String format = DateUtil.format(LocalDate.now(), "yyyy/MM/dd");
        log.debug("{}", format);
    }

}
