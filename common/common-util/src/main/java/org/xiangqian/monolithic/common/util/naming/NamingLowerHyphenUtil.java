package org.xiangqian.monolithic.common.util.naming;

import com.google.common.base.CaseFormat;

/**
 * 连字符命名
 * eg: float max-height;
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingLowerHyphenUtil {

    private static final CaseFormat from = CaseFormat.LOWER_HYPHEN;

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
     * 转为下划线命名
     *
     * @param str
     * @return
     */
    public static String convToLowerUnderscore(String str) {
        return from.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

}
