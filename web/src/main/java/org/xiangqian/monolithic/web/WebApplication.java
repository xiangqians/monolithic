package org.xiangqian.monolithic.web;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author xiangqian
 * @date 19:37 2024/05/29
 */
@ServletComponentScan // 自动扫描和注册Servlet、Filter和Listener等Servlet组件的
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
