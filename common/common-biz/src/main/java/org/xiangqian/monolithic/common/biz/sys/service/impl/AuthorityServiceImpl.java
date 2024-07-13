package org.xiangqian.monolithic.common.biz.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.service.AuthorityService;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.redis.Redis;
import org.xiangqian.monolithic.common.redis.RedisLock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Service
public class AuthorityServiceImpl implements AuthorityService, ApplicationRunner {

    private final String allowsKey = "biz_sys_authority_allows";

    @Autowired
    private Redis redis;

    @Autowired
    private List<AuthorityGroupEntity> authorityGroups;

    @Override
    public boolean isAllow(String method, String path) {
        return redis.Set(allowsKey).isMember(method + path);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedisLock redisLock = redis.Lock(allowsKey + "_lock");
        try {
            if (redisLock.tryLock()) {
                Set<String> allowSet = new HashSet<>(16, 1f);
                for (AuthorityGroupEntity authorityGroup : authorityGroups) {
                    for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                        Byte allow = authority.getAllow();
                        if (Byte.valueOf((byte) 1).equals(allow)) {
                            String method = authority.getMethod();
                            String path = authority.getPath();
                            allowSet.add(method + path);
                        }
                    }
                }
                redis.delete(allowsKey);
                redis.Set(allowsKey).add(allowSet.toArray(String[]::new));
            }
        } finally {
            redisLock.forceUnlock();
        }
    }

}
