package org.monolithic.mapper;

import org.monolithic.po.UserPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPo> {

}
