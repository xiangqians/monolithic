package org.xiangqian.monolithic.common.util.sha;

import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @author xiangqian
 * @date 19:42 2023/11/14
 */
public class Sha2UtilTest {

    @Test
    @SneakyThrows
    public void testEncrypt256ToHex() {
        String data = "Hello, World!";

        String result = Sha2Util.encrypt256ToHex(data);
        System.out.println(result);

        result = Sha2Util.encrypt256ToHexV1(data);
        System.out.println(result);

        result = Sha2Util.encrypt256ToHexV2(data);
        System.out.println(result);

        // dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
        // dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
        // dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
    }

}
