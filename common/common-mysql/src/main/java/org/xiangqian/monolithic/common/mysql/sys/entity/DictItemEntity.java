package org.xiangqian.monolithic.common.mysql.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author xiangqian
 * @date 21:22 2024/07/03
 */
@Data
@TableName("sys_dict_item")
@Schema(description = "字典项表")
public class DictItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "字典类型id")
    private Integer typeId;

    @Schema(description = "名称")
    @TableField("`name`")
    private String name;

    @Schema(description = "值")
    @TableField("`value`")
    private String value;

    @Schema(description = "排序")
    private Short sort;

    @Schema(description = "备注")
    private String rem;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    @TableLogic
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

}
