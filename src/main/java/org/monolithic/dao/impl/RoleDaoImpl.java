package org.monolithic.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.monolithic.annotation.Dao;
import org.monolithic.dao.DaoHelper;
import org.monolithic.dao.RoleDao;
import org.monolithic.mapper.RoleMapper;
import org.monolithic.mapper.UserRoleMapper;
import org.monolithic.pagination.Page;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.RolePo;
import org.monolithic.po.UserRolePo;
import org.monolithic.po.param.RolePoParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 23:05 2022/09/07
 */
@Dao
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Page<RolePo> queryForPage(PageRequest pageRequest, RolePoParam poParam) {
        LambdaQueryWrapper<RolePo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(poParam.getName())) {
            lambdaQueryWrapper.like(RolePo::getName, poParam.getName());
        }
        DaoHelper.sort(lambdaQueryWrapper, pageRequest);
        return DaoHelper.queryForPage(roleMapper, pageRequest, lambdaQueryWrapper);
    }

    @Override
    public List<RolePo> queryForListByUserId(Long userId) {
        List<UserRolePo> userRolePos = userRoleMapper.selectList(new LambdaQueryWrapper<UserRolePo>().eq(UserRolePo::getUserId, userId));
        if (CollectionUtils.isEmpty(userRolePos)) {
            return Collections.emptyList();
        }
        return roleMapper.selectList(new LambdaQueryWrapper<RolePo>().in(RolePo::getId, userRolePos.stream().map(UserRolePo::getRoleId).collect(Collectors.toList())));
    }

    @Override
    public RolePo queryByCode(String code) {
        return roleMapper.selectOne(new LambdaQueryWrapper<RolePo>().eq(RolePo::getCode, code));
    }

    @Override
    public RolePo queryById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Boolean updateById(RolePoParam poParam) {
        return roleMapper.updateById(poParam) > 0;
    }

    @Override
    public Boolean deleteById(Long id) {
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean save(RolePoParam poParam) {
        return roleMapper.insert(poParam) > 0;
    }

}
