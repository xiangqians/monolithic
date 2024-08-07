package org.xiangqian.monolithic.common.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 17:07 2024/06/01
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("org.xiangqian.monolithic.common.mysql.*.mapper") // 扫描 Mapper 接口所在的包路径
public class MysqlAutoConfiguration {
}
