package org.xiangqian.monolithic.common.util.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Rsa密钥对工具
 *
 * @author xiangqian
 * @date 20:58 2023/11/14
 */
public class RsaKeyPairUtil {

    /**
     * 生成512位密钥对
     *
     * @return
     */
    public static KeyPair generate512BitKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(512);
    }

    /**
     * 生成1024位密钥对
     *
     * @return
     */
    public static KeyPair generate1024BitKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(1024);
    }

    /**
     * 生成2048位密钥对
     *
     * @return
     */
    public static KeyPair generate2048BitKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(2048);
    }

    /**
     * 生成4096位密钥对
     *
     * @return
     */
    public static KeyPair generate4096BitKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(4096);
    }

    /**
     * 生成密钥对
     *
     * @param bitLength 密钥长度，单位：bit
     * @return
     * @throws Exception
     */
    private static KeyPair generateKeyPair(int bitLength) throws NoSuchAlgorithmException {
        return generateKeyPair("RSA", bitLength);
    }

    /**
     * 生成密钥对
     *
     * @param algorithm 密钥算法
     * @param bitLength 密钥长度，单位：bit
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static KeyPair generateKeyPair(String algorithm, int bitLength) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(bitLength);
        return generator.generateKeyPair();
    }

    /**
     * 生成密钥对
     *
     * @param algorithm 密钥算法
     * @param provider  密钥提供者
     * @param bitLength 密钥长度，单位：bit
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static KeyPair generateKeyPair(String algorithm, String provider, int bitLength) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, provider);
        generator.initialize(bitLength);
        return generator.generateKeyPair();
    }

}
