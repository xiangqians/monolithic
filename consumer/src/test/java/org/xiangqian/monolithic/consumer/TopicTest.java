package org.xiangqian.monolithic.consumer;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiangqian
 * @date 19:46 2024/06/26
 */
public class TopicTest {

    @Test
    public void te() {
        String input = "/sys/user/str1";
        String regex = "^/sys/user/([^/]+)$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(input);

        // 查找匹配
        if (matcher.find()) {
            System.out.println(matcher.groupCount());
            // 获取匹配的值（捕获组的第一个）
            String matchedValue = matcher.group(1);
            System.out.println("匹配的值: " + matchedValue);
        } else {
            System.out.println("未找到匹配的值");
        }
        System.out.println();

        input = "/sys/user/abc/def";
        regex = "^/sys/user/([^/]+)/([^/]+)$";

        // 编译正则表达式
        pattern = Pattern.compile(regex);

        // 创建匹配器对象
        matcher = pattern.matcher(input);

        // 查找匹配
        if (matcher.find()) {
            System.out.println(matcher.groupCount());
            // 获取匹配的值（捕获组的第一个和第二个）
            String firstVariable = matcher.group(1);
            String secondVariable = matcher.group(2);
            System.out.println("第一个可变字符串: " + firstVariable);
            System.out.println("第二个可变字符串: " + secondVariable);
        } else {
            System.out.println("未找到匹配的值");
        }
    }

}
