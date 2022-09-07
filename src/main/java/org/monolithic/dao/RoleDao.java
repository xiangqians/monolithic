package org.monolithic.dao;

import org.monolithic.pagination.Page;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.RolePo;
import org.monolithic.po.param.RolePoParam;

import java.util.List;

/**
 * @author xiangqian
 * @date 23:01 2022/09/07
 */
public interface RoleDao {

    Page<RolePo> queryForPage(PageRequest pageRequest, RolePoParam poParam);

    List<RolePo> queryForListByUserId(Long userId);

    RolePo queryByCode(String code);

    RolePo queryById(Long id);

    Boolean updateById(RolePoParam poParam);

    Boolean deleteById(Long id);

    Boolean save(RolePoParam poParam);

}
