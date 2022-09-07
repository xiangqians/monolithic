package org.monolithic.resp;

import lombok.Getter;
import lombok.ToString;

/**
 * @author xiangqian
 * @date 20:26:18 2022/03/26
 */
@Getter
@ToString
public enum DefaultStatusCode implements StatusCode {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    INVALID_TOKEN(401, "Invalid Token"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int value;
    private final String reasonPhrase;

    DefaultStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

}
