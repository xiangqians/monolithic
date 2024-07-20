package org.xiangqian.monolithic.common.util;

import org.junit.Test;

/**
 * @author xiangqian
 * @date 20:11 2023/10/19
 */
public class NamingUtilTest {

    @Test
    public void lowerCamel() {
        String str = "testData";
        System.out.println(NamingUtil.lowerCamelConvertToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.lowerCamelConvertToLowerUnderscore(str)); // test_data
        System.out.println(NamingUtil.lowerCamelConvertToLowerHyphen(str)); // test-data
    }

    @Test
    public void upperCamel() {
        String str = "TestData";
        System.out.println(NamingUtil.upperCamelConvertToLowerCamel(str)); // testData
        System.out.println(NamingUtil.upperCamelConvertToLowerUnderscore(str)); // test_data
        System.out.println(NamingUtil.upperCamelConvertToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerUnderscore() {
        String str = "test_data";
        System.out.println(NamingUtil.lowerUnderscoreConvertToLowerCamel(str)); // testData
        System.out.println(NamingUtil.lowerUnderscoreConvertToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.lowerUnderscoreConvertToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerHyphen() {
        String str = "test-data";
        System.out.println(NamingUtil.lowerHyphenConvertToLowerCamel(str)); // testData
        System.out.println(NamingUtil.lowerHyphenConvertToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.lowerHyphenConvertToLowerUnderscore(str)); // test_data
    }

}
