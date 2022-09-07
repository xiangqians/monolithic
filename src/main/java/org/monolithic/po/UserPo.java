package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Data
@TableName("user")
public class UserPo extends ComPo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @TableField("`password`")
    private String password;

    /**
     * 附加信息
     */
    private String addlInfo;

    /**
     * 锁定标记，0-正常，1-锁定
     */
    private String lockFlag;

}
