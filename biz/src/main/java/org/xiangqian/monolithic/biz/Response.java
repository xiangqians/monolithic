package org.xiangqian.monolithic.biz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangqian
 * @date 21:07:15 2022/03/26
 */
@Data
@NoArgsConstructor
@Schema(description = "响应信息")
public class Response<T> {

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    public Response(String code) {
        this(code, null);
    }

    public Response(String code, T data) {
        this(code, CodeDescription.get(code), data);
    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
