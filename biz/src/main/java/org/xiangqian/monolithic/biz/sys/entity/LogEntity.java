package org.xiangqian.monolithic.biz.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Data
@TableName("log")
@Schema(description = "日志信息")
public class LogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "权限id")
    private Long authorityId;

    @Schema(description = "远程地址")
    private String address;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求地址")
    private String url;

    @Schema(description = "请求报文")
    private String body;

    @Schema(description = "状态码")
    @TableField("`code`")
    private String code;

    @Schema(description = "耗时，单位ms")
    @TableField("`time`")
    private Integer time;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

}
