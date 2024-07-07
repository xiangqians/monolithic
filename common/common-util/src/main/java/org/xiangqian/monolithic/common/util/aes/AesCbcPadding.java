package org.xiangqian.monolithic.common.util.aes;

import lombok.Getter;

/**
 * AES/CBC 填充模式
 *
 * @author xiangqian
 * @date 19:29 2024/07/03
 */
public enum AesCbcPadding {
    PKCS5("AES/CBC/PKCS5Padding"),
    ISO10126("AES/CBC/ISO10126Padding"),
    ;

    @Getter
    private final String value;

    AesCbcPadding(String value) {
        this.value = value;
    }

}
