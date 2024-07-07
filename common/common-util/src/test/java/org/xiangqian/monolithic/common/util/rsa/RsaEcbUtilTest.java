package org.xiangqian.monolithic.common.util.rsa;

import lombok.SneakyThrows;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.Base64Util;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * @author xiangqian
 * @date 20:11 2023/11/14
 */
public class RsaEcbUtilTest {

    @Test
    @SneakyThrows
    public void test() {
        // 生成密钥对
        KeyPair keyPair = RsaKeyPairUtil.generate1024BitKeyPair();

        // 明文
        byte[] plaintext = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        encryptAndDecrypt(keyPair, plaintext);
    }

    @Test
    @SneakyThrows
    public void testLonger() {
        // 生成密钥对
        KeyPair keyPair = RsaKeyPairUtil.generate1024BitKeyPair();

        // 明文
        byte[] plaintext = """
                春夜宴桃李园序 / 春夜宴从弟桃花园序
                【作者】李白 【朝代】唐
                夫天地者，万物之逆旅也；光阴者，百代之过客也。
                而浮生若梦，为欢几何？
                古人秉烛夜游，良有以也。
                况阳春召我以烟景，大块假我以文章。
                会桃花之芳园，序天伦之乐事。
                群季俊秀，皆为惠连；吾人咏歌，独惭康乐。
                幽赏未已，高谈转清。
                开琼筵以坐花，飞羽觞而醉月。
                不有佳咏，何伸雅怀？
                如诗不成，罚依金谷酒数。
                """.getBytes(StandardCharsets.UTF_8);

        encryptAndDecrypt(keyPair, plaintext);
    }

    private void encryptAndDecrypt(KeyPair keyPair, byte[] plaintext) {
        encryptAndDecrypt(RsaEcbPadding.DEFAULT, keyPair, plaintext);
        encryptAndDecrypt(RsaEcbPadding.NO, keyPair, plaintext);
        encryptAndDecrypt(RsaEcbPadding.PKCS1, keyPair, plaintext);
        encryptAndDecrypt(RsaEcbPadding.OAEP_WITH_SHA1_AND_MGF1, keyPair, plaintext);
        encryptAndDecrypt(RsaEcbPadding.OAEP_WITH_SHA256_AND_MGF1, keyPair, plaintext);
    }

    private void encryptAndDecrypt(RsaEcbPadding padding, KeyPair keyPair, byte[] plaintext) {
        System.out.println(padding);
        try {
            // 加密
            byte[] ciphertext = RsaEcbUtil.encrypt(padding, keyPair.getPublic(), plaintext);
            System.out.println(Base64Util.encodeToString(ciphertext));

            // 解密
            plaintext = RsaEcbUtil.decrypt(padding, keyPair.getPrivate(), ciphertext);
            System.out.println(new String(plaintext, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}