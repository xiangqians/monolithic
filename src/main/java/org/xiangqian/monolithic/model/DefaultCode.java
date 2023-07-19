package org.xiangqian.monolithic.model;

import lombok.Getter;
import lombok.ToString;

/**
 * @author xiangqian
 * @date 20:53 2023/07/19
 */
@Getter
@ToString
public enum DefaultCode implements Code {

    OK("ok", "OK"),
    BAD_REQUEST("bad_request", "Bad Request"),
    UNAUTHORIZED("unauthorized", "Unauthorized"),
    INVALID_TOKEN("invalid_token", "Invalid Token"),
    NOT_FOUND("not_found", "Not Found"),
    INTERNAL_SERVER_ERROR("internal_server_error", "Internal Server Error"),
    ;

    private final String value;
    private final String reason;

    DefaultCode(String value, String reason) {
        this.value = value;
        this.reason = reason;
    }

}


