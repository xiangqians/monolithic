package org.monolithic.mapper;

import org.monolithic.po.RolePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper 接口
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePo> {

}
