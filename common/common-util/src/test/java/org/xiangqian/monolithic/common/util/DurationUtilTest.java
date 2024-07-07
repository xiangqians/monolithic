package org.xiangqian.monolithic.common.util;

import org.junit.Test;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 11:59 2024/02/27
 */
public class DurationUtilTest {

    private void print(Duration duration) {
        System.out.printf("%s\t%sh\t%sm\t%ss\n", duration, duration.toHours(), duration.toMinutes(), duration.getSeconds());
    }

    @Test
    public void testS() {
        Duration duration = DurationUtil.parse("1s");
        print(duration);

        duration = DurationUtil.parse("60s");
        print(duration);

        duration = DurationUtil.parse("61s");
        print(duration);

        duration = DurationUtil.parse("121s");
        print(duration);
    }

    @Test
    public void testM() {
        Duration duration = DurationUtil.parse("1m");
        print(duration);

        duration = DurationUtil.parse("2m");
        print(duration);
    }

    @Test
    public void testH() {
        Duration duration = DurationUtil.parse("1h");
        print(duration);

        duration = DurationUtil.parse("2h");
        print(duration);
    }

    @Test
    public void testHms() {
        Duration duration = DurationUtil.parse("1h1m1s");
        print(duration);

        duration = DurationUtil.parse("1h1m2s");
        print(duration);
    }

}
