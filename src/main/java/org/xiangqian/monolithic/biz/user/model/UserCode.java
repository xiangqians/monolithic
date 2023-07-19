package org.xiangqian.monolithic.biz.user.model;

import lombok.Getter;
import lombok.ToString;
import org.xiangqian.monolithic.model.Code;

/**
 * @author xiangqian
 * @date 19:27 2023/05/05
 */
@Getter
@ToString
public enum UserCode implements Code {

    OK("ok", "OK"),
    ;

    private final String value;
    private final String reason;

    UserCode(String value, String reason) {
        this.value = "user#" + value;
        this.reason = reason;
    }

}
