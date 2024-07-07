package org.xiangqian.monolithic.common.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 22:56 2024/07/03
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("org.xiangqian.monolithic.common.biz.*.service") // 指定要扫描的service包路径
public class BizAutoConfiguration {

}