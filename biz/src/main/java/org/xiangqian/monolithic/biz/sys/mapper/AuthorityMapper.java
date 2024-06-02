package org.xiangqian.monolithic.biz.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;

/**
 * 权限Mapper
 *
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Mapper
public interface AuthorityMapper extends BaseMapper<AuthorityEntity> {

    AuthorityEntity getOne(AuthorityEntity authority);

    Long updById(AuthorityEntity authority);

}
