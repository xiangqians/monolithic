package org.xiangqian.monolithic.common.util.sha;

import org.junit.Test;

/**
 * @author xiangqian
 * @date 19:42 2023/11/14
 */
public class Sha3UtilTest {

    @Test
    public void testEncrypt256ToHex() {
        String data = "Hello, World!";

        String result = Sha3Util.encrypt256ToHex(data);
        System.out.println(result);

        // 1af17a664e3fa8e419b8ba05c2a173169df76162a5a286e0c405b460d478f7ef
    }

}
