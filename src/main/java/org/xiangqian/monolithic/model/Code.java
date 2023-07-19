package org.xiangqian.monolithic.model;


/**
 * 响应码
 *
 * @author xiangqian
 * @date 19:21 2023/04/11
 */
public interface Code {

    /**
     * @return 响应值
     */
    String getValue();

    /**
     * @return 响应原因短语
     */
    String getReason();

}
