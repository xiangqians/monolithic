package org.xiangqian.monolithic.webmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author xiangqian
 * @date 19:37 2024/05/29
 */
@ServletComponentScan // 自动扫描和注册Servlet、Filter和Listener等Servlet组件的
@SpringBootApplication
public class WebmvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebmvcApplication.class, args);
    }

}
