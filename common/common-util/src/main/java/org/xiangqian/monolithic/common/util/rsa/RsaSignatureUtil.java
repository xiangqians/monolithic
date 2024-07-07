package org.xiangqian.monolithic.common.util.rsa;

import java.security.*;

/**
 * RSA签名工具
 * <p>
 * RSA签名的主要用途包括以下几个方面：
 * 1、数据完整性验证：RSA签名可以验证数据的完整性，确保数据在传输过程中没有被篡改。接收方使用发送方的公钥来验证数据的签名，如果验证通过，则可以确定数据未被篡改。
 * 2、身份认证：RSA签名可以用于验证数据的发送者身份。发送方使用自己的私钥对数据进行签名，接收方使用发送方的公钥来验证签名的有效性，从而确认数据确实来自预期的发送者。
 * 3、防止抵赖：RSA签名可以防止数据发送者在发送数据后否认其发送过该数据。因为只有发送者持有相应的私钥才能生成有效的签名，其他人无法伪造有效的签名来欺骗接收方。
 * 4、安全通信：在安全通信中，RSA签名通常与对称加密结合使用，用于建立安全信道之前验证通信双方的身份，以及在通信过程中确保消息的完整性和认证性。
 * 5、数字证书：RSA签名在数字证书中广泛应用，用于验证证书的真实性和完整性。数字证书是用于认证和安全通信的重要基础，RSA签名帮助确保证书的可信度。
 * 总之，RSA签名是一种重要的加密技术，通过非对称加密的方式实现数据的安全性、完整性和可信性，广泛应用于网络通信、电子商务、数字证书等领域。
 * <p>
 * RSA签名和数据发送的基本过程：
 * 1、生成签名：发送方使用自己的私钥（通常是RSA私钥对）对原始数据进行签名。
 * 2、发送数据：发送方将原始数据和生成的签名值一起发送给接收方。
 * 3、验证签名：接收方收到数据和签名值后，使用发送方的公钥（对应的RSA公钥对）和签名值对原始数据进行验签，以验证原始数据完整性
 *
 * @author xiangqian
 * @date 19:29 2024/01/26
 */
public class RsaSignatureUtil {

    /**
     * 生成签名（使用私钥生成签名）
     *
     * @param privateKey 私钥
     * @param data       待签名的数据
     * @return 签名值
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static byte[] sign(RsaSignatureAlgorithm algorithm, PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = getSignature(algorithm);
        s.initSign(privateKey);
        s.update(data);
        return s.sign();
    }

    /**
     * 验证签名（使用公钥验证签名）
     *
     * @param publicKey 公钥
     * @param data      待验证的数据
     * @param signature 签名值
     * @return
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verify(RsaSignatureAlgorithm algorithm, PublicKey publicKey, byte[] data, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = getSignature(algorithm);
        s.initVerify(publicKey);
        s.update(data);
        return s.verify(signature);
    }

    /**
     * 获取签名实例
     *
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static Signature getSignature(RsaSignatureAlgorithm algorithm) throws NoSuchAlgorithmException {
        return Signature.getInstance(algorithm.getValue());
    }

}
