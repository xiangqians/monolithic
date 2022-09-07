package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.monolithic.o.Po;
import lombok.Data;

/**
 * 角色表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("role")
public class RolePo extends ComPo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 角色码
     */
    @TableField("`code`")
    private String code;

}
