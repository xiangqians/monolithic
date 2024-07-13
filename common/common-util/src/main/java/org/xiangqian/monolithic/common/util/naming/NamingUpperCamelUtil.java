package org.xiangqian.monolithic.common.util.naming;

import com.google.common.base.CaseFormat;

/**
 * 帕斯卡命名法，又叫大驼峰式命名法。与小驼峰式命名法的最大区别在于，每个单词的第一个字母都要大写。
 * eg: float MaxHeight;
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingUpperCamelUtil {

    private static final CaseFormat from = CaseFormat.UPPER_CAMEL;

    /**
     * 转为小驼峰式命名
     *
     * @param str
     * @return
     */
    public static String convToLowerCamel(String str) {
        return from.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 转为下划线命名
     *
     * @param str
     * @return
     */
    public static String convToLowerUnderscore(String str) {
        return from.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    /**
     * 转为连字符命名
     *
     * @param str
     * @return
     */
    public static String convToLowerHyphen(String str) {
        return from.to(CaseFormat.LOWER_HYPHEN, str);
    }

}
