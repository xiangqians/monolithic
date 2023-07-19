package org.xiangqian.monolithic.biz.sys.model;

import lombok.Getter;
import lombok.ToString;
import org.xiangqian.monolithic.model.Code;

/**
 * @author xiangqian
 * @date 21:40 2023/05/05
 */
@Getter
@ToString
public enum SysCode implements Code {

    ;

    private final String value;
    private final String reason;

    SysCode(String value, String reason) {
        this.value = value;
        this.reason = reason;
    }

}
