package org.monolithic.mapper;

import org.monolithic.po.UserRolePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色表 Mapper 接口
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePo> {

}
