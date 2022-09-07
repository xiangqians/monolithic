package org.monolithic.dao;

import org.monolithic.po.PermPo;

import java.util.List;

/**
 * @author xiangqian
 * @date 23:11 2022/09/07
 */
public interface PermDao {

    List<PermPo> queryForListByRoleId(Long roleId);

}
