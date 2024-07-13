package org.xiangqian.monolithic.common.util.naming;

import com.google.common.base.CaseFormat;

/**
 * 下划线命名法，通过下划线来分割全部都是大写的单词。下划线命名法在宏定义和常量中使用比较多。
 * eg: float max_height;
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingLowerUnderscoreUtil {

    private static final CaseFormat from = CaseFormat.LOWER_UNDERSCORE;

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
     * 转为大驼峰式命名
     *
     * @param str
     * @return
     */
    public static String convToUpperCamel(String str) {
        return from.to(CaseFormat.UPPER_CAMEL, str);
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
