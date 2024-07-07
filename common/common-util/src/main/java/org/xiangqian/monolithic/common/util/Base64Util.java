package org.xiangqian.monolithic.common.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Base64 编解码工具
 *
 * @author xiangqian
 * @date 19:29 2024/01/24
 */
public class Base64Util {

    public static byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static byte[] encode(SecretKey secretKey) {
        return encode(secretKey.getEncoded());
    }

    public static byte[] encode(PublicKey publicKey) {
        return encode(publicKey.getEncoded());
    }

    public static byte[] encode(PrivateKey privateKey) {
        return encode(privateKey.getEncoded());
    }

    public static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String encodeToString(SecretKey secretKey) {
        return encodeToString(secretKey.getEncoded());
    }

    public static String encodeToString(PublicKey publicKey) {
        return encodeToString(publicKey.getEncoded());
    }

    public static String encodeToString(PrivateKey privateKey) {
        return encodeToString(privateKey.getEncoded());
    }

    public static String encodeToHexString(byte[] bytes) {
        return Hex.encodeHexString(encode(bytes));
    }

    public static String encodeToHexString(SecretKey secretKey) {
        return encodeToHexString(secretKey.getEncoded());
    }

    public static String encodeToHexString(PublicKey publicKey) {
        return encodeToHexString(publicKey.getEncoded());
    }

    public static String encodeToHexString(PrivateKey privateKey) {
        return encodeToHexString(privateKey.getEncoded());
    }

    public static byte[] decode(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    public static SecretKey decodeToSecretKey(byte[] bytes) {
        return createSecretKeySpec(decode(bytes));
    }

    public static PublicKey decodeToPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createPublicKey(decode(bytes));
    }

    public static PrivateKey decodeToPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createPrivateKey(decode(bytes));
    }

    public static byte[] decodeString(String string) {
        return Base64.getDecoder().decode(string);
    }

    public static SecretKey decodeStringToSecretKey(String string) {
        return createSecretKeySpec(decodeString(string));
    }

    public static PublicKey decodeStringToPublicKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createPublicKey(decodeString(string));
    }

    public static PrivateKey decodeStringToPrivateKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createPrivateKey(decodeString(string));
    }

    public static byte[] decodeHexString(String hexString) throws DecoderException {
        return decode(Hex.decodeHex(hexString));
    }

    public static SecretKey decodeHexStringToSecretKey(String hexString) throws DecoderException {
        return createSecretKeySpec(decodeHexString(hexString));
    }

    public static PublicKey decodeHexStringToPublicKey(String hexString) throws DecoderException, NoSuchAlgorithmException, InvalidKeySpecException {
        return createPublicKey(decodeHexString(hexString));
    }

    public static PrivateKey decodeHexStringToPrivateKey(String hexString) throws DecoderException, NoSuchAlgorithmException, InvalidKeySpecException {
        return createPrivateKey(decodeHexString(hexString));
    }

    private static SecretKeySpec createSecretKeySpec(byte[] encodedKey) {
        return new SecretKeySpec(encodedKey, "AES");
    }

    private static PublicKey createPublicKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey createPrivateKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

}
