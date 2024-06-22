package org.xiangqian.monolithic.common.util;

/**
 * @author xiangqian
 * @date 21:22 2024/01/02
 */
public class JsUtilTest {

    public static void main(String[] args) {
        Object result = JsUtil.execute("sum(100, 1)", """
                function sum(i1, i2){
                    return i1 + i2
                }
                """);
        System.out.println(result.getClass());
        System.out.println(result);
    }

}
