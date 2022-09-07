package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.monolithic.o.Po;

import java.time.LocalDateTime;

/**
 * 角色权限表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("role_perm")
public class RolePermPo implements Po {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 权限id
     */
    private Long permId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
