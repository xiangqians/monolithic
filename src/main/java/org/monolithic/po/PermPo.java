package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("perm")
public class PermPo extends ComPo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限父id
     */
    private Long parentId;

    /**
     * 权限名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 权限允许权限方法，GET、POST、PUT、DELETE
     */
    private String method;

    /**
     * 权限路径
     */
    @TableField("`path`")
    private String path;

}
