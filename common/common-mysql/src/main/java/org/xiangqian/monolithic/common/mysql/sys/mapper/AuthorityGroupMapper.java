package org.xiangqian.monolithic.common.mysql.sys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;

/**
 * 权限组表Mapper
 *
 * @author xiangqian
 * @date 20:38 2024/06/12
 */
@Mapper
public interface AuthorityGroupMapper {

    AuthorityGroupEntity getOne(AuthorityGroupEntity entity);

    Long updById(AuthorityGroupEntity entity);

}
