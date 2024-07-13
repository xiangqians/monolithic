package org.xiangqian.monolithic.common.util.naming;

import com.google.common.base.CaseFormat;

/**
 * 驼峰式命名法，又叫小驼峰式命名法。要求第一个单词首字母小写，后面其他单词首字母大写。
 * eg: float maxHeight;
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingLowerCamelUtil {

    private static final CaseFormat from = CaseFormat.LOWER_CAMEL;

    /**
     * 转为大驼峰式命名
     *
     * @param str
     * @return
     */
    public static String convToUpperCamel(String str) {
        return from.to(CaseFormat.UPPER_CAMEL, str);
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
