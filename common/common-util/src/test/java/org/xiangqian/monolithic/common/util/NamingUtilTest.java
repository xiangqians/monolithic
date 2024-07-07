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
        System.out.println(NamingUtil.LowerCamel.convToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.LowerCamel.convToLowerUnderscore(str)); // test_data
        System.out.println(NamingUtil.LowerCamel.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void upperCamel() {
        String str = "TestData";
        System.out.println(NamingUtil.UpperCamel.convToLowerCamel(str)); // testData
        System.out.println(NamingUtil.UpperCamel.convToLowerUnderscore(str)); // test_data
        System.out.println(NamingUtil.UpperCamel.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerUnderscore() {
        String str = "test_data";
        System.out.println(NamingUtil.LowerUnderscore.convToLowerCamel(str)); // testData
        System.out.println(NamingUtil.LowerUnderscore.convToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.LowerUnderscore.convToLowerHyphen(str)); // test-data
    }

    @Test
    public void lowerHyphen() {
        String str = "test-data";
        System.out.println(NamingUtil.LowerHyphen.convToLowerCamel(str)); // testData
        System.out.println(NamingUtil.LowerHyphen.convToUpperCamel(str)); // TestData
        System.out.println(NamingUtil.LowerHyphen.convToLowerUnderscore(str)); // test_data
    }

}
