package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.monolithic.o.Po;
import lombok.Data;

/**
 * 用户角色表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("user_role")
public class UserRolePo implements Po {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
