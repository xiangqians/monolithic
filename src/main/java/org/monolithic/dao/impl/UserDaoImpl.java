package org.monolithic.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.monolithic.annotation.Dao;
import org.monolithic.dao.DaoHelper;
import org.monolithic.dao.UserDao;
import org.monolithic.mapper.UserMapper;
import org.monolithic.pagination.Page;
import org.monolithic.pagination.PageRequest;
import org.monolithic.po.UserPo;
import org.monolithic.po.param.UserPoParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

/**
 * @author xiangqian
 * @date 22:41 2022/09/06
 */
@Dao
public class UserDaoImpl implements UserDao {

    private final String CACHE_NAME = "CACHE_DAO_USER";

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<UserPo> queryForPage(PageRequest pageRequest, UserPoParam poParam) {
        LambdaQueryWrapper<UserPo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(poParam.getUsername())) {
            lambdaQueryWrapper.like(UserPo::getUsername, poParam.getUsername());
        }
        DaoHelper.sort(lambdaQueryWrapper, pageRequest);
        return DaoHelper.queryForPage(userMapper, pageRequest, lambdaQueryWrapper);
    }

    @Override
    public UserPo queryByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserPo>().eq(UserPo::getUsername, username));
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "'id_'+#id", unless = "#result == null")
    @Override
    public UserPo queryById(Long id) {
        return userMapper.selectById(id);
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "'id_'+#poParam.id")
    @Override
    public Boolean updateById(UserPoParam poParam) {
        return userMapper.updateById(poParam) > 0;
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "'id_'+#id")
    @Override
    public Boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean save(UserPoParam poParam) {
        return userMapper.insert(poParam) > 0;
    }

}
