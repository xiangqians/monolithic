package org.xiangqian.monolithic.common.util.rsa;

import lombok.Getter;

/**
 * RSA/ECB 填充模式
 *
 * @author xiangqian
 * @date 20:58 2023/11/14
 */
public enum RsaEcbPadding {
    /**
     * RSA
     */
    DEFAULT("RSA"),

    /**
     * RSA/ECB/NoPadding
     * 不使用填充方式，加密和解密的输入必须是固定长度的整数倍。填充时会在分组内容的前面填充0，直到内容的位数和模数相同。例如2048位的RSA，需要填充至256个字节。
     */
    NO("RSA/ECB/NoPadding"),

    /**
     * RSA/ECB/PKCS1Padding
     * 最常见的模式，使用RSA算法进行加密和解密，并使用PKCS1填充方式进行填充。
     */
    PKCS1("RSA/ECB/PKCS1Padding"),

    /**
     * RSA/ECB/OAEPWithSHA-1AndMGF1Padding
     * 算法使用OAEP填充方式，在SHA-1哈希函数和MGF1掩码生成函数的基础上进行加密和解密。
     * <p>
     * 使用OAEP（Optimal Asymmetric Encryption Padding）填充方案。
     * OAEP填充方案相对更安全，并且在实践中被广泛使用。
     */
    OAEP_WITH_SHA1_AND_MGF1("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),

    /**
     * RSA/ECB/OAEPWithSHA-256AndMGF1Padding
     * 算法也使用OAEP填充方式，但使用SHA-256哈希函数和MGF1掩码生成函数进行加密和解密
     */
    OAEP_WITH_SHA256_AND_MGF1("RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),

    ;

    @Getter
    private final String value;

    RsaEcbPadding(String value) {
        this.value = value;
    }

}
