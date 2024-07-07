package org.xiangqian.monolithic.common.util.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA是一种非对称加密算法，最初由Ron Rivest、Adi Shamir和Leonard Adleman在1977年提出。
 * <p>
 * RSA加密和解密过程：
 * 1、加密：发送方使用接收方的公钥对数据进行加密。
 * 2、解密：接收方使用自己持有的私钥对加密后的数据进行解密。
 *
 * @author xiangqian
 * @date 20:58 2023/11/14
 */
public class RsaEcbUtil {

    /**
     * 加密（公钥加密）
     *
     * @param padding
     * @param publicKey 公钥
     * @param plaintext 明文
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(RsaEcbPadding padding, PublicKey publicKey, byte[] plaintext) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] result = block(padding, Cipher.ENCRYPT_MODE, publicKey, plaintext);
        if (result != null) {
            return result;
        }

        Cipher cipher = getCipher(padding);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    /**
     * 解密（私钥解密）
     *
     * @param padding
     * @param privateKey 私钥
     * @param ciphertext 密文
     * @return 明文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(RsaEcbPadding padding, PrivateKey privateKey, byte[] ciphertext) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] result = block(padding, Cipher.DECRYPT_MODE, privateKey, ciphertext);
        if (result != null) {
            return result;
        }

        Cipher cipher = getCipher(padding);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    /**
     * RSA加密算法对数据块的长度是有限制的：
     * 1、对于公钥加密，数据块的最大长度不能超过密钥长度减去11字节。
     * 2、对于私钥解密，数据块的最大长度等于密钥长度。
     * 这意味着在使用1024位（128个字节）密钥时，公钥加密的数据块最大长度为117个字节，私钥解密的数据块最大长度为128个字节；
     * 而在使用2048位（256个字节）密钥时，公钥加密的数据块最大长度为245个字节，私钥解密的数据块最大长度为256个字节。
     *
     * @param opmode
     * @param key
     * @param data
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static byte[] block(RsaEcbPadding padding, int opmode, Key key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 密钥长度，单位：byte
        int keyLength = 0;

        // 数据长度，单位：byte
        int dataLength = data.length;

        // 最大块长度，单位：byte
        int maxBlockLength = 0;

        // 公钥
        if (key instanceof RSAPublicKey) {
            keyLength = ((RSAPublicKey) key).getModulus().bitLength() / 8;
            maxBlockLength = keyLength - 11;
        }
        // 私钥
        else if (key instanceof RSAPrivateKey) {
            keyLength = ((RSAPrivateKey) key).getModulus().bitLength() / 8;
            maxBlockLength = keyLength;
        }

        // 分块加密/解密
        if (dataLength > maxBlockLength) {
            Cipher cipher = getCipher(padding);
            cipher.init(opmode, key);

            // 已加密/解密的数据
            int arrIndex = 0;
            int arrLength = dataLength / maxBlockLength;
            if (dataLength % maxBlockLength != 0) {
                arrLength += 1;
            }
            byte[][] arr = new byte[arrLength][];
            int resultLength = 0;

            // 加密
            int offset = 0;
            while (offset < dataLength) {
                byte[] bytes = null;
                int nextOffset = offset + maxBlockLength;
                if (nextOffset <= dataLength) {
                    bytes = cipher.doFinal(data, offset, maxBlockLength);
                } else {
                    bytes = cipher.doFinal(data, offset, dataLength - offset);
                }
                arr[arrIndex++] = bytes;
                resultLength += bytes.length;
                offset = nextOffset;
            }

            // 合并结果
            int resultIndex = 0;
            byte[] result = new byte[resultLength];
            for (int i = 0; i < arrLength; i++) {
                byte[] bytes = arr[i];
                int length = bytes.length;
                System.arraycopy(bytes, 0, result, resultIndex, length);
                resultIndex += length;
            }
            return result;
        }

        return null;
    }

    private static Cipher getCipher(RsaEcbPadding padding) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(padding.getValue());
    }

}
