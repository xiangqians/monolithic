package org.xiangqian.monolithic.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author xiangqian
 * @date 21:07:15 2022/03/26
 */
@Getter
@Schema(description = "响应信息")
public class Resp<T> {

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    private Resp(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public static class Builder<T> {

        private Code code;
        private String msg;
        private T data;

        private Builder() {
        }

        public Builder<T> code(Code code) {
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Resp<T> build() {
            Assert.notNull(code, "code不能为空");
            return new Resp(code.getValue(), Optional.ofNullable(msg).orElseGet(code::getReason), data);
        }
    }

}
