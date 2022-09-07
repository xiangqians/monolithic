package org.monolithic.service;

import org.monolithic.pagination.Page;
import org.monolithic.po.UserPo;
import org.monolithic.vo.user.UserVo;
import org.monolithic.vo.user.param.UserAddVoParam;
import org.monolithic.vo.user.param.UserModifyVoParam;
import org.monolithic.vo.user.param.UserPageVoParam;

/**
 * 用户表 服务类
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
public interface UserService {

    Page<UserVo> queryForPage(UserPageVoParam voParam);

    UserVo queryById(Long id);

    Boolean updateById(UserModifyVoParam voParam);

    Boolean deleteById(Long id);

    Boolean save(UserAddVoParam voParam);

}
