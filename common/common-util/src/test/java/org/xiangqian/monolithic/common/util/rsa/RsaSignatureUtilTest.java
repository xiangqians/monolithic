package org.xiangqian.monolithic.common.util.rsa;

import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author xiangqian
 * @date 20:11 2023/11/14
 */
public class RsaSignatureUtilTest {

    @Test
    public void test() throws Exception {
        // 生成密钥对
//        KeyPair keyPair = RsaKeyPairUtil.generate1024BitKeyPair();

        // 512bit
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKOM99j80MTMYfZGJLSl8TiGHTzK5n2XOAC4x60QF/qcMgasqxlryfL+yijUVeeo5cxTWO/LzRep2C52gpMBPRMCAwEAAQ==";
        String privateKey = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAo4z32PzQxMxh9kYktKXxOIYdPMrmfZc4ALjHrRAX+pwyBqyrGWvJ8v7KKNRV56jlzFNY78vNF6nYLnaCkwE9EwIDAQABAkAOk3mNWwQniJ2cdiY+/AvHwVrUDbNFcyzjEj99gucthwltOJ66eBq7ua6iWiQ5DIKXdHcXmOBmj8TTDpra0dDdAiEAqiTPtX2CGmszgGDX8VfJywVFp3YbAGKZRRU7tAuqpO8CIQD2FHRr2XTi2tNBtxAgmoEoUoX5PGOq2DGgfv1vVahSHQIhAKAgENMvkXgnl8qkWdaq1iEZjxoxTuwn3zNi7lNd/3VFAiEA8Soyvs+rAfi7OZgTMiC5m67kpdJNS039tPE/pzEnePECIQCpgncsZZGd0N3YbKrXUk5iYRHyc3IrdcWmUc2SR1gsOQ==";
        KeyPair keyPair = new KeyPair(Base64Util.decodeStringToPublicKey(publicKey), Base64Util.decodeStringToPrivateKey(privateKey));

        // 明文
        byte[] data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        signAndVerify(RsaSignatureAlgorithm.MD5, keyPair, data);
        signAndVerify(RsaSignatureAlgorithm.SHA1, keyPair, data);
        signAndVerify(RsaSignatureAlgorithm.SHA256, keyPair, data);
    }

    private void signAndVerify(RsaSignatureAlgorithm algorithm, KeyPair keyPair, byte[] data) throws Exception {
        System.out.println(algorithm);

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 生成签名
        byte[] signature = RsaSignatureUtil.sign(algorithm, privateKey, data);
        System.out.println(Base64Util.encodeToString(signature));

        // 验证签名
        boolean verified = RsaSignatureUtil.verify(algorithm, publicKey, data, signature);
        System.out.println(verified);
        System.out.println();
    }

}