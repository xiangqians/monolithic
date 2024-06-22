package org.xiangqian.monolithic.common.db.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;

/**
 * 用户表Mapper
 *
 * @author xiangqian
 * @date 22:18 2024/05/30
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
