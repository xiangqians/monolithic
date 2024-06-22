package org.xiangqian.monolithic.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

/**
 * 邮件
 * https://springdoc.cn/spring-boot-email
 * <p>
 * 邮件涉及的两个传输协议：
 * 1、SMTP协议：发送邮件，通常把处理用户smtp请求（邮件发送请求）的服务器称之为SMTP服务器（邮件发送服务器）
 * 2、POP3协议：接收邮件，通常把处理用户pop3请求（邮件接收请求）的服务器称之为POP3服务器（邮件接收服务器）
 *
 * @author xiangqian
 * @date 19:13 2024/06/04
 */
public class Mail {

    /**
     * 邮件发送器
     */
    private JavaMailSender javaMailSender;

    /**
     * 发件人邮箱地址
     */
    private String from;

    /**
     * @param javaMailSender 邮件发送器
     * @param from           发件人邮箱地址
     */
    public Mail(JavaMailSender javaMailSender, String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param text    邮件正文
     */
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param text    邮件正文
     * @param html    邮件正文是否是HTML
     */
    public void send(String to, String subject, String text, boolean html) throws MessagingException {
        send(to, subject, text, html, null);
    }

    /**
     * 发送邮件
     *
     * @param to          收件人邮箱地址
     * @param subject     邮件主题
     * @param text        邮件正文
     * @param html        邮件正文是否是HTML
     * @param attachments 邮件附件
     */
    public void send(String to, String subject, String text, boolean html, List<Attachment> attachments) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        boolean multipart = CollectionUtils.isNotEmpty(attachments);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, html);
        if (multipart) {
            for (Attachment attachment : attachments) {
                // 添加附件
                helper.addAttachment(attachment.getName(), attachment.getInputStreamSource(), attachment.getContentType());
            }
        }
        javaMailSender.send(mimeMessage);
    }

    /**
     * 邮件附件
     */
    @Data
    public static class Attachment {
        /**
         * 附件名称
         */
        private String name;

        /**
         * 附件输入流
         */
        private InputStreamSource inputStreamSource;

        /**
         * 附件内容类型
         */
        private String contentType;

        public Attachment(String name, InputStreamSource inputStreamSource) {
            this(name, inputStreamSource, "application/octet-stream");
        }

        public Attachment(String name, InputStreamSource inputStreamSource, String contentType) {
            this.name = name;
            this.inputStreamSource = inputStreamSource;
            this.contentType = contentType;
        }
    }

}
