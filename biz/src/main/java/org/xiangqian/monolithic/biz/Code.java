package org.xiangqian.monolithic.biz;

/**
 * 状态码
 *
 * @author xiangqian
 * @date 14:48 2024/06/01
 */
public interface Code {

    @Description("成功")
    String OK = "ok";

    @Description("未找到")
    String NOT_FOUND = "not_found";

    @Description("未授权")
    String UNAUTHORIZED = "unauthorized";

    @Description("方法不被允许")
    String METHOD_NOT_ALLOWED = "method_not_allowed";

    @Description("没有权限访问")
    String FORBIDDEN = "forbidden";

    @Description("错误的请求")
    String BAD_REQUEST = "bad_request";

    @Description("网络异常")
    String ERROR = "error";

}
