package org.xiangqian.monolithic.common.util;

import lombok.SneakyThrows;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 19:43 2024/01/25
 */
public class Base64UtilTest {

    @Test
    public void testString() {
        byte[] bytes = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        // 编码
        String string = Base64Util.encodeToString(bytes);
        System.out.println(string);

        // 解码
        bytes = Base64Util.decodeString(string);
        System.out.println(new String(bytes));
    }

    @Test
    @SneakyThrows
    public void testHexString() {
        byte[] bytes = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        // 编码
        String hexString = Base64Util.encodeToHexString(bytes);
        System.out.println(hexString);

        // 解码
        bytes = Base64Util.decodeHexString(hexString);
        System.out.println(new String(bytes));
    }

}
