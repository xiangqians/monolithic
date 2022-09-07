package org.monolithic.dao;

import org.monolithic.po.AuthClientDetailsPo;

/**
 * @author xiangqian
 * @date 23:12 2022/09/06
 */
public interface AuthClientDetailsDao {

    AuthClientDetailsPo queryByClientId(String clientId);

}
