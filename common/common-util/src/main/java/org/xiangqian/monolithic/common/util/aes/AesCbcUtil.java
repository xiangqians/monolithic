package org.xiangqian.monolithic.common.util.aes;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES/CBC（Advanced Encryption Standard / Cipher Block Chaining）是一种对称加密算法及其工作模式。在使用AES/CBC时，填充（Padding）模式是必需的，用于在加密前将数据填充到块的大小。
 *
 * @author xiangqian
 * @date 19:08 2024/01/25
 */
public class AesCbcUtil {

    /**
     * 加密
     *
     * @param padding
     * @param secretKey 密钥
     * @param iv        初始化向量（Initialization Vector，IV）
     * @param plaintext 明文
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(AesCbcPadding padding, SecretKey secretKey, byte[] iv, byte[] plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(padding);
        IvParameterSpec params = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
        return cipher.doFinal(plaintext);
    }

    /**
     * 解密
     *
     * @param padding
     * @param secretKey  密钥
     * @param iv         初始化向量（Initialization Vector，IV）
     * @param ciphertext 密文
     * @return 明文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(AesCbcPadding padding, SecretKey secretKey, byte[] iv, byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(padding);
        IvParameterSpec params = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        return cipher.doFinal(ciphertext);
    }

    private static Cipher getCipher(AesCbcPadding padding) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(padding.getValue());
    }

}