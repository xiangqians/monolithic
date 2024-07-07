package org.xiangqian.monolithic.common.util.aes;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES/ECB 模式加解密
 * <p>
 * AES（Advanced Encryption Standard）是一种常用的对称加密算法，常用的加密模式有以下几种：
 * 1、ECB（Electronic Codebook）模式：将明文分成固定大小的块，每个块独立地进行加密。但是，ECB模式存在一个问题，如果明文中有相同的块，则加密后的结果也会相同，这导致了一些安全性问题。
 * 2、CBC（Cipher Block Chaining）模式：在CBC模式中，前一个密文块会与当前明文块进行异或操作，然后再进行加密。这种方式可以解决ECB模式的问题，提高了安全性。
 * 3、CFB（Cipher Feedback）模式：CFB模式将前一个密文块作为输入来加密当前明文块，并将输出的密文块与明文块进行异或操作得到密文。这种模式实现了反馈机制，使得加密更加灵活。
 * 4、OFB（Output Feedback）模式：OFB模式类似于CFB模式，但它使用密钥流而不是密文块来进行加密。密钥流生成器产生的密钥流与明文进行异或操作得到密文。
 * 5、CTR（Counter）模式：CTR模式将一个计数器作为输入与密钥流生成器结合，然后将生成的密钥流与明文进行异或操作得到密文。CTR模式具有高度的并行性，因此在多核处理器上能够获得更好的性能。
 * <p>
 * 注：
 * AES/ECB 模式下存在安全隐患，因此推荐使用更安全的分组密码模式，如 CBC、CTR 或 GCM。
 *
 * @author xiangqian
 * @date 19:00 2024/01/24
 */
public class AesEcbUtil {

    /**
     * 加密
     *
     * @param padding
     * @param secretKey 密钥
     * @param plaintext 明文
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] encrypt(AesEcbPadding padding, SecretKey secretKey, byte[] plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = getCipher(padding);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plaintext);
    }

    /**
     * 解密
     *
     * @param padding
     * @param secretKey  密钥
     * @param ciphertext 密文
     * @return 明文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] decrypt(AesEcbPadding padding, SecretKey secretKey, byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = getCipher(padding);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(ciphertext);
    }

    private static Cipher getCipher(AesEcbPadding padding) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(padding.getValue());
    }

}