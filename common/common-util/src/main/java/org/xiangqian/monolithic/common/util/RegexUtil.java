package org.xiangqian.monolithic.common.util;

import org.apache.commons.collections4.map.LRUMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiangqian
 * @date 19:18 2024/06/26
 */
public class RegexUtil {

    private static final Map<String, Pattern> patternMap = new LRUMap<>(512, 1f);

    private static Pattern getPattern(String regex) {
        Pattern pattern = patternMap.get(regex);
        if (pattern == null) {
            synchronized (RegexUtil.class) {
                if (pattern == null) {
                    // 编译正则表达式
                    pattern = Pattern.compile(regex);
                    patternMap.put(regex, pattern);
                }
            }
        }
        return pattern;
    }

    /**
     * 判断字符串是否匹配指定的正则表达式
     *
     * @param regex 匹配的正则表达式
     * @param input 待匹配的字符串
     * @return
     */
    public static boolean isMatch(String regex, String input) {
        Pattern pattern = getPattern(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * 提取正则表达式中的捕获组值
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 匹配到的捕获组值列表
     */
    public static String[] extractValues(String regex, String input) {
        Pattern pattern = getPattern(regex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(input);

        // 查找匹配
        if (matcher.find()) {
            // 获取捕获组的数量
            int groupCount = matcher.groupCount();
            String[] values = new String[groupCount];

            // 提取每个捕获组的值
            for (int i = 0; i < groupCount; i++) {
                values[i] = matcher.group(i + 1);
            }

            return values;
        }

        return null;
    }

}
