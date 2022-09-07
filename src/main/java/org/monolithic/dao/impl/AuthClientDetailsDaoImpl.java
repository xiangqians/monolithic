package org.monolithic.dao.impl;

import org.monolithic.annotation.Dao;
import org.monolithic.dao.AuthClientDetailsDao;
import org.monolithic.mapper.AuthClientDetailsMapper;
import org.monolithic.po.AuthClientDetailsPo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiangqian
 * @date 23:58 2022/09/06
 */
@Dao
public class AuthClientDetailsDaoImpl implements AuthClientDetailsDao {

    @Autowired
    private AuthClientDetailsMapper authClientDetailsMapper;

    @Override
    public AuthClientDetailsPo queryByClientId(String clientId) {
        return authClientDetailsMapper.selectById(clientId);
    }

}
