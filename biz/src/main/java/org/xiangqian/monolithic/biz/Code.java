package org.xiangqian.monolithic.biz;

/**
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

    @Description("不支持请求方法")
    String REQUEST_METHOD_NOT_SUPPORTED = "request_method_not_supported";

    @Description("网络异常")
    String ERROR = "error";

}
