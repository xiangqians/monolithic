package org.xiangqian.monolithic;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiangqian
 * @date 21:12 2023/03/28
 */
@SpringBootApplication(
        // 排除默认 springdoc ui 配置类（SwaggerUiConfigParameters），使用自定义配置类（CustomSwaggerUiConfigParameters）
        exclude = SwaggerUiConfigParameters.class
)
public class Application {

    // org.xiangqian.monolithic
    public static final String BASE_PKG = Application.class.getPackageName();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // 监控
    // NotEmpty去除前后空格

}
