package org.xiangqian.monolithic.common.util.rsa;//package org.xiangqian.monolithic.common.util;

import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author xiangqian
 * @date 20:11 2023/11/14
 */
public class RsaKeyPairUtilTest {

    @Test
    public void keyPair() throws Exception {
        KeyPair keyPair = RsaKeyPairUtil.generate512BitKeyPair();

        // 公钥
        PublicKey publicKey = keyPair.getPublic();
        System.out.println(publicKey);
        System.out.println();
        String encodedPublicKey = Base64Util.encodeToString(publicKey);
        System.out.println(encodedPublicKey);
        System.out.println();
        publicKey = Base64Util.decodeStringToPublicKey(encodedPublicKey);
        System.out.println(publicKey);

        System.out.println();
        System.out.println("----------------------");
        System.out.println();

        // 私钥
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println(privateKey);
        System.out.println();
        String encodedPrivateKey = Base64Util.encodeToString(privateKey);
        System.out.println(encodedPrivateKey);
        System.out.println();
        privateKey = Base64Util.decodeStringToPrivateKey(encodedPrivateKey);
        System.out.println(privateKey);
    }

}