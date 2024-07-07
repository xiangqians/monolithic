package org.xiangqian.monolithic.common.util.aes;

import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 19:58 2024/01/24
 */
public class AesEcbUtilTest {

    @Test
    public void test() throws Exception {
        // 密钥
        String encodedKey = "+0u9BQACkxS2hEL3/XQlfw==";
        SecretKey key = Base64Util.decodeStringToSecretKey(encodedKey);

        // 明文
        byte[] plaintext = "Hello, World!!!!".getBytes(StandardCharsets.UTF_8);

        encryptAndDecrypt(AesEcbPadding.NO, key, plaintext);
        encryptAndDecrypt(AesEcbPadding.PKCS5, key, plaintext);
//        encryptAndDecrypt(AesEcbPadding.PKCS7, key, plaintext);
        encryptAndDecrypt(AesEcbPadding.ISO10126, key, plaintext);
//        encryptAndDecrypt(AesEcbPadding.X923, key, plaintext);
    }

    // 加密和解密
    private void encryptAndDecrypt(AesEcbPadding padding, SecretKey secretKey, byte[] plaintext) throws Exception {
        System.out.println(padding);

        // 加密
        byte[] ciphertext = AesEcbUtil.encrypt(padding, secretKey, plaintext);
        System.out.println(Base64Util.encodeToString(ciphertext));

        // 解密
        plaintext = AesEcbUtil.decrypt(padding, secretKey, ciphertext);
        System.out.println(new String(plaintext, StandardCharsets.UTF_8));
        System.out.println();
    }

}
