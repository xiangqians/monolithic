package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.monolithic.o.Po;

import java.time.LocalDateTime;

/**
 * 系统日志信息表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("sys_log")
public class SysLogPo implements Po {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求ip
     */
    private String reqIp;

    /**
     * 请求端口
     */
    private String reqPort;

    /**
     * 请求参数
     */
    private String reqParam;

    /**
     * 请求所需要的时间，ms
     */
    private Integer reqdTime;

    /**
     * 浏览器名称
     */
    private String browsName;

    /**
     * 浏览器类型
     */
    private String browsType;

    /**
     * 浏览器家族
     */
    private String browsGroup;

    /**
     * 浏览器生产厂商
     */
    private String browsMfr;

    /**
     * 浏览器使用的渲染引擎
     */
    private String browsRe;

    /**
     * 浏览器版本
     */
    private String browsVer;

    /**
     * 操作系统名
     */
    private String osName;

    /**
     * 访问设备类型
     */
    private String osDeviceType;

    /**
     * 操作系统家族
     */
    private String osGroup;

    /**
     * 操作系统生产厂商
     */
    private String osMfr;

    /**
     * 操作用户id
     */
    private Integer userId;

    /**
     * 操作权限id
     */
    private String permId;

    /**
     * 异常标识，0-正常，1-异常
     */
    private String excFlag;

    /**
     * 异常信息
     */
    private String excMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
