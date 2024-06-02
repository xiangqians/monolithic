package org.xiangqian.monolithic.biz;

import com.google.common.base.CaseFormat;

/**
 * 命名工具
 *
 * @author xiangqian
 * @date 20:32 2023/10/19
 */
public class NamingUtil {

    /**
     * 小驼峰式命名
     */
    public static final LowerCamel LowerCamel = new LowerCamelImpl();

    /**
     * 大驼峰式命名
     */
    public static final UpperCamel UpperCamel = new UpperCamelImpl();

    /**
     * 下划线命名
     */
    public static final LowerUnderscore LowerUnderscore = new LowerUnderscoreImpl();

    /**
     * 连字符命名
     */
    public static final LowerHyphen LowerHyphen = new LowerHyphenImpl();

    private static class LowerCamelImpl extends Abs implements LowerCamel {
        public LowerCamelImpl() {
            super(CaseFormat.LOWER_CAMEL);
        }
    }

    /**
     * 小驼峰式命名
     * <p>
     * 驼峰式命名法，又叫小驼峰式命名法。要求第一个单词首字母小写，后面其他单词首字母大写。
     * eg: float maxHeight;
     */
    public static interface LowerCamel {
        /**
         * 转为大驼峰式命名
         *
         * @param str
         * @return
         */
        String convToUpperCamel(String str);

        /**
         * 转为下划线命名
         *
         * @param str
         * @return
         */
        String convToLowerUnderscore(String str);

        /**
         * 转为连字符命名
         *
         * @param str
         * @return
         */
        String convToLowerHyphen(String str);
    }

    private static class UpperCamelImpl extends Abs implements UpperCamel {
        public UpperCamelImpl() {
            super(CaseFormat.UPPER_CAMEL);
        }
    }

    /**
     * 大驼峰式命名
     * <p>
     * 帕斯卡命名法，又叫大驼峰式命名法。与小驼峰式命名法的最大区别在于，每个单词的第一个字母都要大写。
     * eg: float MaxHeight;
     */
    public static interface UpperCamel {
        /**
         * 转为小驼峰式命名
         *
         * @param str
         * @return
         */
        String convToLowerCamel(String str);

        /**
         * 转为下划线命名
         *
         * @param str
         * @return
         */
        String convToLowerUnderscore(String str);

        /**
         * 转为连字符命名
         *
         * @param str
         * @return
         */
        String convToLowerHyphen(String str);
    }

    private static class LowerUnderscoreImpl extends Abs implements LowerUnderscore {
        public LowerUnderscoreImpl() {
            super(CaseFormat.LOWER_UNDERSCORE);
        }
    }

    /**
     * 下划线命名
     * <p>
     * 下划线命名法，通过下划线来分割全部都是大写的单词。下划线命名法在宏定义和常量中使用比较多。
     * eg: float max_height;
     */
    public static interface LowerUnderscore {
        /**
         * 转为小驼峰式命名
         *
         * @param str
         * @return
         */
        String convToLowerCamel(String str);

        /**
         * 转为大驼峰式命名
         *
         * @param str
         * @return
         */
        String convToUpperCamel(String str);

        /**
         * 转为连字符命名
         *
         * @param str
         * @return
         */
        String convToLowerHyphen(String str);
    }

    private static class LowerHyphenImpl extends Abs implements LowerHyphen {
        public LowerHyphenImpl() {
            super(CaseFormat.LOWER_HYPHEN);
        }
    }

    /**
     * 连字符命名
     * <p>
     * 连字符命名
     * eg: float max-height;
     */
    public static interface LowerHyphen {
        /**
         * 转为小驼峰式命名
         *
         * @param str
         * @return
         */
        String convToLowerCamel(String str);

        /**
         * 转为大驼峰式命名
         *
         * @param str
         * @return
         */
        String convToUpperCamel(String str);

        /**
         * 转为下划线命名
         *
         * @param str
         * @return
         */
        String convToLowerUnderscore(String str);
    }

    private static abstract class Abs {
        private CaseFormat from;

        public Abs(CaseFormat from) {
            this.from = from;
        }

        public String convToLowerCamel(String str) {
            return conv(str, from, CaseFormat.LOWER_CAMEL);
        }

        public String convToUpperCamel(String str) {
            return conv(str, from, CaseFormat.UPPER_CAMEL);
        }

        public String convToLowerUnderscore(String str) {
            return conv(str, from, CaseFormat.LOWER_UNDERSCORE);
        }

        public String convToLowerHyphen(String str) {
            return conv(str, from, CaseFormat.LOWER_HYPHEN);
        }

        /**
         * 命名转换
         *
         * @param str  字符串
         * @param from 原命名
         * @param to   目标命名
         * @return
         */
        private String conv(String str, CaseFormat from, CaseFormat to) {
            return from.to(to, str);
        }
    }

}
