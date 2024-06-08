package org.xiangqian.monolithic.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * CRC
 * 计算CRC-16校验码
 * <p>
 * 在线校验工具
 * https://crccalc.com/
 * http://www.ip33.com/crc.html
 *
 * @author xiangqian
 * @date 19:25 2023/04/06
 */
public class CRC16Util {

    public static int modbus(String hex) throws DecoderException {
        byte[] bytes = Hex.decodeHex(hex);
        return modbus(bytes);
    }

    /**
     * CRC-16/MODBUS
     * x16 + x15 + x2 + 1
     * Algorithm        Poly	Init	RefIn	RefOut	XorOut
     * CRC-16/MODBUS    0x8005	0xFFFF	true	true	0x0000
     * <p>
     * ModBus 通信协议的 CRC。
     * 冗余循环校验码含2个字节, 即 16 位二进制数
     * CRC 码由发送设备计算, 放置于所发送信息帧的尾部。接收信息设备再重新计算所接收信息 (除 CRC 之外的部分）的 CRC, 比较计算得到的 CRC 是否与接收到CRC相符, 如果两者不相符, 则认为数据出错。
     *
     * @param bytes
     * @return
     */
    public static int modbus(byte[] bytes) {
        // 1、预置 1 个 16 位的寄存器为十六进制 0xFFFF（二进制数为：1111 1111 1111 1111）, 称此寄存器为 CRC寄存器。
        long crc = 0xFFFF; // init
        int poly = 0xA001; // 多项式，0x8005 反转就是 0xA001
        for (byte b : bytes) {
            // 2、把第一个 8 位二进制数据 (通信信息帧的第一个字节) 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器。
            crc ^= b & 0xFF; // 0xFF（二进制数：1111 1111）byte转为int

            // 3、把 CRC 寄存器的内容右移一位(朝低位)用 0 填补最高位, 并检查右移后的移出位。
            // 4、如果移出位为 0, 重复第 3 步 ( 再次右移一位); 如果移出位为 1, CRC 寄存器与多项式A001 ( 1010 0000 0000 0001) 进行异或。
            // 多项式 0xA001（1010 0000 0000 0001） 是 0x8005（1000 0000 0000 0101） 按位颠倒后的结果。
            // 5、重复步骤 3 和步骤 4, 直到右移 8 次,这样整个8位数据全部进行了处理。
            for (int i = 0; i < 8; i++) { // 8 bit
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= poly;
                } else {
                    crc >>= 1;
                }
            }
        }

        // 6、重复步骤 2 到步骤 5, 进行通信信息帧下一个字节的处理。

        // 7、将该通信信息帧所有字节按上述步骤计算完成后,得到的16位CRC寄存器的高、低字节进行交换。
        // 0xFF00 二进制数：1111 1111 0000 0000
        // 0x00FF 二进制数：0000 0000 1111 1111
        crc = (crc << 8 & 0xFF00) | (crc >> 8 & 0x00FF);

        // 8、最后得到的 CRC寄存器内容即为 CRC码。
        return (int) crc;
    }

}
