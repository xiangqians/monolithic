package org.monolithic.mapper;

import org.monolithic.po.AuthClientDetailsPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户端详情 Mapper 接口
 *
 * @author xiangqian
 * @date 23:06 2022/09/06
 */
@Mapper
public interface AuthClientDetailsMapper extends BaseMapper<AuthClientDetailsPo> {

}
