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
        // /sys/user/：匹配确切的字符序列 "/sys/user/"
        // .+：匹配任意字符（除了换行符以外的任何字符），一次或多次
        // /authorities：匹配确切的字符序列 "/authorities"
        String regex = "/sys/user/(.+)/authorities";

        String input = "/sys/user/1/authorities";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));

        input = "0/sys/user/1/authorities/2";
        values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

    @Test
    public void testExtractValues2() {
        // ^ 表示匹配输入的开始位置
        // /sys/user/ 匹配确切的字符序列 "/sys/user/"
        // ^/sys/user/ 匹配确切前缀部分 "/sys/user/"
        // ([^/]+) 是一个捕获组，表示匹配任意非空的字符序列，但不包括斜杠 /
        // /authorities 匹配确切的字符序列 "/authorities"
        // $ 表示匹配输入的结束位置
        // /authorities$ 匹配固定的后缀部分 "/authorities"
        String regex = "^/sys/user/([^/]+)/authorities$";

        String input = "/sys/user/1/authorities";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));

        input = "0/sys/user/1/authorities/2";
        values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

    @Test
    public void testExtractValues3() {
        // /sys/user/ 匹配确切的字符序列 "/sys/user/"
        // ([^/]+) 是一个捕获组，表示匹配任意非空的字符序列，但不包括斜杠 /
        String regex = "/sys/user/([^/]+)";
        String input = "/sys/user/1";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

    @Test
    public void testExtractValues4() {
        // /sys/user/ 匹配固定的文本前缀
        // ([^/]+) 第一个捕获组，匹配除了斜杠 / 外的任意字符序列。
        // (?:/([^/]+))* 非捕获组，匹配零个或多个斜杠 / 后跟一个捕获组 ([^/]+)
        String regex = "/sys/user/([^/]+)(?:/([^/]+))*";

        String input = "/sys/user/1/authorities";
        String[] values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));

        input = "/sys/user/1/authorities/2";
        values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));

        input = "/sys/user/1/authorities/2/3";
        values = RegexUtil.extractValues(regex, input);
        System.out.println(Arrays.toString(values));
    }

}
