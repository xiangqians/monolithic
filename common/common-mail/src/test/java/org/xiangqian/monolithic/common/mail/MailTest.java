package org.xiangqian.monolithic.common.mail;

import jakarta.mail.MessagingException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author xiangqian
 * @date 13:19 2024/06/07
 */
public class MailTest {

    public static void main(String[] args) throws MessagingException {
        Mail mail = null;
        mail.send("test@example.com", "测试", "测试内容");
        mail.send("test@example.com", "Hello", "Hello <strong> World</strong>！", true);
        mail.send("test@example.com", "测试附件", "<strong>请查看附件</strong>", true,
                List.of(new Attachment("test.txt", () -> Files.newInputStream(Paths.get("E:\\tmp\\test.txt")))));
    }

}
