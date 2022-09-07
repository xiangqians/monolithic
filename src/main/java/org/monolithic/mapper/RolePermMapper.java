package org.monolithic.mapper;

import org.monolithic.po.RolePermPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限表 Mapper 接口
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Mapper
public interface RolePermMapper extends BaseMapper<RolePermPo> {

}
