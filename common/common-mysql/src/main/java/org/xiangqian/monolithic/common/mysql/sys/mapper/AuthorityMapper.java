package org.xiangqian.monolithic.common.mysql.sys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;

import java.util.List;

/**
 * 权限表Mapper
 *
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Mapper
public interface AuthorityMapper {

    List<AuthorityEntity> list();

    AuthorityEntity getOne(AuthorityEntity entity);

    Long updById(AuthorityEntity entity);

}
