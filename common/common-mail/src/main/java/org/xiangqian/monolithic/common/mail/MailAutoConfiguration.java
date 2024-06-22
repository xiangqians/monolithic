package org.xiangqian.monolithic.common.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author xiangqian
 * @date 15:10 2024/06/10
 */
@Configuration(proxyBeanMethods = false)
public class MailAutoConfiguration {

    @Bean
    public Mail mail(JavaMailSender javaMailSender, @Value("${spring.mail.from}") String from) {
        return new Mail(javaMailSender, from);
    }

}
