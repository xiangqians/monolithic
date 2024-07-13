package org.xiangqian.monolithic.common.util;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiangqian
 * @date 20:50 2023/10/18
 */
public class HttpTest {

    @Test
    public void test() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        Http http = new Http(new RestTemplate(), headers);

        String string = http.get("https://www.baidu.com", null, String.class);
        System.out.println(string);
    }

}
