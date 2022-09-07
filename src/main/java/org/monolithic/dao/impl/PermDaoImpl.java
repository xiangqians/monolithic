package org.monolithic.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.monolithic.annotation.Dao;
import org.monolithic.dao.PermDao;
import org.monolithic.mapper.PermMapper;
import org.monolithic.mapper.RolePermMapper;
import org.monolithic.po.PermPo;
import org.monolithic.po.RolePermPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 23:12 2022/09/07
 */
@Dao
public class PermDaoImpl implements PermDao {

    @Autowired
    private PermMapper permMapper;

    @Autowired
    private RolePermMapper rolePermMapper;

    @Override
    public List<PermPo> queryForListByRoleId(Long roleId) {
        List<RolePermPo> rolePermPos = rolePermMapper.selectList(new LambdaQueryWrapper<RolePermPo>().eq(RolePermPo::getRoleId, roleId));
        if (CollectionUtils.isEmpty(rolePermPos)) {
            return Collections.emptyList();
        }
//        return permMapper.selectList(new LambdaQueryWrapper<PermPo>().in(PermPo::getId, rolePermPos.stream().map(RolePermPo::getPermId).collect(Collectors.toList())));
        return rolePermPos.stream().map(rolePermPo -> {
            PermPo po = new PermPo();
            po.setId(rolePermPo.getPermId());
            return po;
        }).collect(Collectors.toList());
    }

}
