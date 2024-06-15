package org.xiangqian.monolithic.biz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.xiangqian.monolithic.util.Mail;

/**
 * @author xiangqian
 * @date 15:10 2024/06/10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.mail.host")
public class MailAutoConfiguration {

    @Bean
    public Mail mail(JavaMailSender javaMailSender, @Value("${spring.mail.from}") String from) {
        return new Mail(javaMailSender, from);
    }

}
