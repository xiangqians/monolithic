package org.xiangqian.monolithic.common.mysql.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface AuthorityMapper extends BaseMapper<AuthorityEntity> {

    List<AuthorityEntity> list();

    AuthorityEntity getOne(AuthorityEntity authority);

    Long updById(AuthorityEntity authority);

}
