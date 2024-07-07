package org.xiangqian.monolithic.common.util.aes;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author xiangqian
 * @date 14:57 2022/08/02
 */
public class AesKeyUtil {

    /**
     * 生成128位密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generate128BitKey() throws NoSuchAlgorithmException {
        return generateKey(128);
    }

    /**
     * 生成192位密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generate192BitKey() throws NoSuchAlgorithmException {
        return generateKey(192);
    }

    /**
     * 生成256位密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generate256BitKey() throws NoSuchAlgorithmException {
        return generateKey(256);
    }

    private static SecretKey generateKey(int bitLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(bitLength, new SecureRandom());
        return keyGenerator.generateKey();
    }

}
