package org.xiangqian.monolithic.common.util.naming;

import org.junit.Test;

/**
 * @author xiangqian
 * @date 20:11 2023/10/19
 */
public class NamingUtilTest {

    @Test
    public void lowerCamel() {
        String str = "testData";
        System.out.println(NamingLowerCamelUtil.convToUpperCamel(str)); // TestData
        System.out.println(NamingLowerCamelUtil.convToLowerUnderscore(str)); // test_data
        System.out.println(NamingLowerCamelUtil.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void upperCamel() {
        String str = "TestData";
        System.out.println(NamingUpperCamelUtil.convToLowerCamel(str)); // testData
        System.out.println(NamingUpperCamelUtil.convToLowerUnderscore(str)); // test_data
        System.out.println(NamingUpperCamelUtil.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerUnderscore() {
        String str = "test_data";
        System.out.println(NamingLowerUnderscoreUtil.convToLowerCamel(str)); // testData
        System.out.println(NamingLowerUnderscoreUtil.convToUpperCamel(str)); // TestData
        System.out.println(NamingLowerUnderscoreUtil.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerHyphen() {
        String str = "test-data";
        System.out.println(NamingLowerHyphenUtil.convToLowerCamel(str)); // testData
        System.out.println(NamingLowerHyphenUtil.convToUpperCamel(str)); // TestData
        System.out.println(NamingLowerHyphenUtil.convToLowerUnderscore(str)); // test_data
    }

}
