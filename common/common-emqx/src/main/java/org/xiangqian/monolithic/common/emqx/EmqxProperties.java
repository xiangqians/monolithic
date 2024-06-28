package org.xiangqian.monolithic.common.emqx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangqian
 * @date 21:57 2024/06/28
 */
@Data
@ConfigurationProperties(prefix = "emqx")
public class EmqxProperties {

    private String broker;
    private String clientId;
    private String user;
    private String passwd;

}
