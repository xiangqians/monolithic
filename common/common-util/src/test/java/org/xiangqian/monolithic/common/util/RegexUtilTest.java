package org.xiangqian.monolithic.common.util;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author xiangqian
 * @date 19:25 2024/06/26
 */
public class RegexUtilTest {

    @Test
    public void testExtractValues1() {
        String regex = "^/sys/user/([^/]+)$";
        String input = "/sys/user/1";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

    @Test
    public void testExtractValues2() {
        String input = "/sys/user/1/authorities";
        String regex = "^/sys/user/([^/]+)/([^/]+)$";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

}
