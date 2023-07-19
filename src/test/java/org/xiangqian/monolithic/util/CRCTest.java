package org.xiangqian.monolithic.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.xiangqian.monolithic.util.crc.CRC16Util;

/**
 * @author xiangqian
 * @date 19:21 2023/04/06
 */
@Slf4j
public class CRCTest {

    @Test
    public void test() throws Exception {
        // 0x00 0x14 0x15 0xef 0x01 0x00 0x11 0x00 0x09 0x40 0xc4 0xe0 0x64 0x1d 0x5a 0x0f 0x21 0x78
        String hex = "0xef 0x01 0x00 0x11 0x00 0x09 0x40 0xc4 0xe0 0x64 0x1d 0x5a 0x0f";
//        hex = "0x01 0x10 0x12 0x34 0x56 0x78";
//        hex = "ef 01 00 11 0x01 0x10 0x12 0x34 0x56 0x78";
//        hex = "0x01 0x10 0x12 0x34 0x56 0x78 0x78 0x56 0x78 0x78 09 40";
        hex = "ef 01 00 11 00 09 40 c4";
//        hex = "A1 00 11 00 09 40 c4 e0 64";
//        hex = "ef 01 00 11 00";

        // 00 23 15
        // ef 01 00 11 00 04 41 11
        // 53 5d
//        hex ="ef 01 00 11 00 04 41 11";

        hex = hex.replace("0x", "");
        log.debug("{}", hex);
        hex = hex.replace(" ", "");
        log.debug("{}", hex);

        int crc = CRC16Util.modbus(hex);
        log.debug("{}", crc);
        log.debug("{}", Integer.toHexString(crc));
        log.debug("{}", StringUtils.leftPad(Integer.toHexString(crc), 4, "0"));
        log.debug("{}", String.format("%04X", crc));
    }

}
