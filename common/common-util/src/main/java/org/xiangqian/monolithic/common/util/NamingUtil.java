package org.xiangqian.monolithic.common.util;

import com.google.common.base.CaseFormat;

/**
 * 命名工具
 * <p>
 * 驼峰式命名法，又叫小驼峰式命名法。要求第一个单词首字母小写，后面其他单词首字母大写。
 * eg: float maxHeight;
 * <p>
 * 帕斯卡命名法，又叫大驼峰式命名法。与小驼峰式命名法的最大区别在于，每个单词的第一个字母都要大写。
 * eg: float MaxHeight;
 * <p>
 * 下划线命名法，通过下划线来分割全部都是大写的单词。下划线命名法在宏定义和常量中使用比较多。
 * eg: float max_height;
 * <p>
 * 连字符命名
 * eg: float max-height;
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingUtil {

    /**
     * 小驼峰命名转为大驼峰命名
     *
     * @param str
     * @return
     */
    public static String lowerCamelConvertToUpperCamel(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, str);
    }

    /**
     * 小驼峰命名转为下划线命名
     *
     * @param str
     * @return
     */
    public static String lowerCamelConvertToLowerUnderscore(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    /**
     * 小驼峰命名转为连字符命名
     *
     * @param str
     * @return
     */
    public static String lowerCamelConvertToLowerHyphen(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
    }

    /**
     * 大驼峰命名法转为小驼峰命名
     *
     * @param str
     * @return
     */
    public static String upperCamelConvertToLowerCamel(String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 大驼峰命名法转为下划线命名
     *
     * @param str
     * @return
     */
    public static String upperCamelConvertToLowerUnderscore(String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    /**
     * 大驼峰命名法转为连字符命名
     *
     * @param str
     * @return
     */
    public static String upperCamelConvertToLowerHyphen(String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
    }


    /**
     * 下划线命名转为小驼峰命名
     *
     * @param str
     * @return
     */
    public static String lowerUnderscoreConvertToLowerCamel(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 下划线命名转为大驼峰命名
     *
     * @param str
     * @return
     */
    public static String lowerUnderscoreConvertToUpperCamel(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str);
    }

    /**
     * 下划线命名转为连字符命名
     *
     * @param str
     * @return
     */
    public static String lowerUnderscoreConvertToLowerHyphen(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, str);
    }

    /**
     * 连字符命名转为小驼峰命名
     *
     * @param str
     * @return
     */
    public static String lowerHyphenConvertToLowerCamel(String str) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 连字符命名转为大驼峰命名
     *
     * @param str
     * @return
     */
    public static String lowerHyphenConvertToUpperCamel(String str) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, str);
    }

    /**
     * 连字符命名转为下划线命名
     *
     * @param str
     * @return
     */
    public static String lowerHyphenConvertToLowerUnderscore(String str) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

}
