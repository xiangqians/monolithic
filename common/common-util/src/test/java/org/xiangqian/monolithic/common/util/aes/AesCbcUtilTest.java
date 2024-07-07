package org.xiangqian.monolithic.common.util.aes;

import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 19:58 2024/01/24
 */
public class AesCbcUtilTest {

    @Test
    public void test() throws Exception {
        // 密钥（128bit）
        String encodedKey = "r9S4vkJrmpy3HQwfvwmLhQ==";
        SecretKey secretKey = Base64Util.decodeStringToSecretKey(encodedKey);

        // 16字节的初始化向量
        final byte[] iv = "fedcba9876543210".getBytes(StandardCharsets.UTF_8);

        // 明文
        byte[] plaintext = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        encryptAndDecrypt(AesCbcPadding.PKCS5, secretKey, iv, plaintext);
        encryptAndDecrypt(AesCbcPadding.ISO10126, secretKey, iv, plaintext);
    }

    // 加密和解密
    private void encryptAndDecrypt(AesCbcPadding padding, SecretKey secretKey, byte[] iv, byte[] plaintext) throws Exception {
        System.out.println(padding);

        // 加密
        byte[] ciphertext = AesCbcUtil.encrypt(padding, secretKey, iv, plaintext);
        System.out.println(Base64Util.encodeToString(ciphertext));

        // 解密
        plaintext = AesCbcUtil.decrypt(padding, secretKey, iv, ciphertext);
        System.out.println(new String(plaintext, StandardCharsets.UTF_8));
        System.out.println();
    }

}
