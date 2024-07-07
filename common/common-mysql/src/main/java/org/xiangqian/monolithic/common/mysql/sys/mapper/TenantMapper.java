package org.xiangqian.monolithic.common.mysql.sys.mapper;

import org.xiangqian.monolithic.common.mysql.sys.entity.TenantEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户表Mapper
 *
 * @author xiangqian
 * @date 21:22 2024/07/03
 */
@Mapper
public interface TenantMapper extends BaseMapper<TenantEntity> {

}
