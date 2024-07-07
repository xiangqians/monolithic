package org.xiangqian.monolithic.common.util.rsa;

import lombok.Getter;

/**
 * RSA签名算法
 *
 * @author xiangqian
 * @date 19:29 2024/01/26
 */
public enum RsaSignatureAlgorithm {
    MD5("MD5withRSA"),
    SHA1("SHA1withRSA"),
    SHA256("SHA256withRSA"),
    ;

    @Getter
    private final String value;

    RsaSignatureAlgorithm(String value) {
        this.value = value;
    }

}
