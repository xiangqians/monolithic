package org.xiangqian.monolithic.common.util.aes;

import lombok.Getter;

/**
 * AES/ECB 填充模式
 *
 * @author xiangqian
 * @date 19:00 2024/01/24
 */
public enum AesEcbPadding {
    /**
     * 该模式不进行任何填充，要求明文长度必须是块长度的整数倍。
     */
    NO("AES/ECB/NoPadding"),

    /**
     * PKCS5Padding 和 PKCS7Padding：这两种模式采用标准的 PKCS#5 和 PKCS#7 填充方式，根据块的大小进行适当的填充。
     */
    PKCS5("AES/ECB/PKCS5Padding"),

    PKCS7("AES/ECB/PKCS7Padding"),

    /**
     * 该模式使用随机的填充字节，并在最后一个字节中包含填充的字节数。
     */
    ISO10126("AES/ECB/ISO10126Padding"),

    /**
     * 该模式将填充字节设置为 0x00，并在最后一个字节中包含填充的字节数。
     */
    X923("AES/ECB/X9.23Padding"),
    ;

    @Getter
    private final String value;

    AesEcbPadding(String value) {
        this.value = value;
    }

}
