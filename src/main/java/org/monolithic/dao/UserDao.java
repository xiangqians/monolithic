package org.monolithic.dao;

import org.monolithic.pagination.Page;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.UserPo;
import org.monolithic.po.param.UserPoParam;

/**
 * @author xiangqian
 * @date 22:37 2022/09/06
 */
public interface UserDao {

    Page<UserPo> queryForPage(PageRequest pageRequest, UserPoParam poParam);

    UserPo queryByUsername(String username);

    UserPo queryById(Long id);

    Boolean updateById(UserPoParam poParam);

    Boolean deleteById(Long id);

    Boolean save(UserPoParam poParam);

}
