package org.xiangqian.monolithic.common.mail;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

/**
 * 邮件附件
 *
 * @author xiangqian
 * @date 10:57 2024/06/30
 */
@Data
public class Attachment {

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
