package org.xiangqian.monolithic.common.mysql.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;

/**
 * 权限组表Mapper
 *
 * @author xiangqian
 * @date 20:38 2024/06/12
 */
@Mapper
public interface AuthorityGroupMapper extends BaseMapper<AuthorityGroupEntity> {

    AuthorityGroupEntity getOne(AuthorityGroupEntity authorityGroup);

    Long updById(AuthorityGroupEntity authorityGroup);

}
