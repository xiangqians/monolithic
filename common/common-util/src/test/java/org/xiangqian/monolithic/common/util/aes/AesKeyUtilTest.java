package org.xiangqian.monolithic.common.util.aes;

import lombok.SneakyThrows;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import javax.crypto.SecretKey;

/**
 * @author xiangqian
 * @date 19:58 2024/01/24
 */
public class AesKeyUtilTest {

    @Test
    @SneakyThrows
    public void test() {
        SecretKey secretKey = AesKeyUtil.generate128BitKey();
        System.out.println(secretKey);

        String encodedKey = Base64Util.encodeToString(secretKey);
        System.out.println(encodedKey);

        secretKey = Base64Util.decodeStringToSecretKey(encodedKey);
        System.out.println(secretKey);
    }

}
