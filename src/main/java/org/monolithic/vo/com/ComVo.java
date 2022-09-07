package org.monolithic.vo.com;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.monolithic.o.Vo;

import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 22:20 2022/08/16
 */
@Data
public class ComVo implements Vo {

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("创建时间，时间格式：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间，时间格式：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
