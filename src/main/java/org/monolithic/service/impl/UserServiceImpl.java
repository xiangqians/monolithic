package org.monolithic.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.monolithic.dao.UserDao;
import org.monolithic.pagination.Page;
import org.monolithic.po.UserPo;
import org.monolithic.po.param.UserPoParam;
import org.monolithic.service.UserService;
import org.monolithic.vo.user.UserVo;
import org.monolithic.vo.user.param.UserAddVoParam;
import org.monolithic.vo.user.param.UserModifyVoParam;
import org.monolithic.vo.user.param.UserPageVoParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    private Function<UserPo, UserVo> poToVoFunction = po -> po.convertToVo(UserVo.class);

    @Transactional(timeout = 10, readOnly = true)
    @Override
    public Page<UserVo> queryForPage(UserPageVoParam voParam) {
        return userDao.queryForPage(voParam, voParam.convertToPoParam(UserPoParam.class)).convert(poToVoFunction);
    }

    @Transactional(timeout = 10, readOnly = true)
    @Override
    public UserVo queryById(Long id) {
        UserPo po = checkUserId(id);
        return poToVoFunction.apply(po);
    }

    @Transactional(timeout = 10)
    @Override
    public Boolean updateById(UserModifyVoParam voParam) {
        checkUserId(voParam.getId());
        UserPoParam poParam = voParam.convertToPoParam(UserPoParam.class);
        poParam.setPassword(passwordEncoder.encode(voParam.getPassword()));
        return userDao.updateById(poParam);
    }

    @Transactional(timeout = 10)
    @Override
    public Boolean deleteById(Long id) {
        checkUserId(id);
        return userDao.deleteById(id);
    }

    @Transactional(timeout = 10)
    @Override
    public Boolean save(UserAddVoParam voParam) {
        UserPoParam poParam = voParam.convertToPoParam(UserPoParam.class);
        poParam.setPassword(passwordEncoder.encode(voParam.getPassword()));
        return userDao.save(poParam);
    }

    @Transactional(timeout = 10, readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }

        UserPo userPo = userDao.queryByUsername(username);
        if (Objects.isNull(userPo)) {
            throw new UsernameNotFoundException("用户不存在");
        }

        String password = userPo.getPassword();
        boolean enabled = "0".equals(userPo.getDelFlag());
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = "0".equals(userPo.getLockFlag());
        // 存放 role & permission
        List<GrantedAuthority> authorities = new ArrayList<>();
//        // role
//        Long roleId = userPo.getRoleId();
//        if (Objects.nonNull(roleId) && roleId > 0) {
//            RolePo rolePo = roleDao.queryById(roleId);
//            if (Objects.nonNull(rolePo)) {
//                authorities.add(SecurityUtils.createGrantedAuthorityByRoleCode(rolePo.getCode()));
//
//                // permission
//                List<MenuPo> menuPos = menuDao.queryForListByRoleId(roleId);
//                if (CollectionUtils.isNotEmpty(menuPos)) {
//                    for (MenuPo menuPo : menuPos) {
//                        authorities.add(SecurityUtils.createGrantedAuthorityByPermId(menuPo.getPermId()));
//                    }
//                }
//            }
//        }
        User user = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return user;
    }

    private UserPo checkUserId(Long userId) {
        Assert.notNull(userId, "用户id不能为空");
        UserPo po = null;
        Assert.isTrue(userId > 0 && Objects.nonNull(po = userDao.queryById(userId)),
                String.format("用户信息不存在", userId));
        return po;
    }

}
