package org.xiangqian.monolithic.common.mysql.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Data
@Schema(description = "日志信息")
public class LogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "权限id")
    private Long authorityId;

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "远程地址")
    private String remoteAddress;

    @Schema(description = "请求地址")
    private String reqUrl;

    @Schema(description = "请求报文")
    private String reqBody;

    @Schema(description = "耗时，单位ms")
    private Integer duration;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

}