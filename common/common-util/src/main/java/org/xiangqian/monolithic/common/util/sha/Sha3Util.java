package org.xiangqian.monolithic.common.util.sha;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * SHA-3
 * 这是最新的版本，由Keccak算法获胜，并被选为新的SHA-3标准。SHA-3并不是SHA-2的延伸，而是一个全新的算法，提供了与SHA-2不同的设计和安全性特征。
 *
 * @author xiangqian
 * @date 20:37 2023/11/14
 */
public class Sha3Util {

    public static byte[] encrypt256(byte[] data) {
        return DigestUtils.sha3_256(data);
    }

    public static byte[] encrypt256(InputStream data) throws IOException {
        return DigestUtils.sha3_256(data);
    }

    public static byte[] encrypt256(String data) {
        return DigestUtils.sha3_256(data);
    }

    public static String encrypt256ToHex(byte[] data) {
        return DigestUtils.sha3_256Hex(data);
    }

    public static String encrypt256ToHex(InputStream data) throws IOException {
        return DigestUtils.sha3_256Hex(data);
    }

    public static String encrypt256ToHex(String data) {
        return DigestUtils.sha3_256Hex(data);
    }

}
