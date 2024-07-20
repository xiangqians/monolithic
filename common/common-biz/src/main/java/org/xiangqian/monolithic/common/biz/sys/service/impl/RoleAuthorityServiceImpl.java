package org.xiangqian.monolithic.common.biz.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.service.RoleAuthorityService;
import org.xiangqian.monolithic.common.mysql.sys.mapper.RoleAuthorityMapper;
import org.xiangqian.monolithic.common.redis.Redis;
import org.xiangqian.monolithic.common.redis.RedisBitmap;

import java.util.Set;

/**
 * @author xiangqian
 * @date 11:34 2024/06/10
 */
@Service
public class RoleAuthorityServiceImpl implements RoleAuthorityService, ApplicationRunner {

    @Autowired
    private Redis redis;

    @Autowired
    private RoleAuthorityMapper roleAuthorityMapper;

    @Override
    public void add(Long roleId, Set<Long> authorityIds) {
        String key = getKey(roleId);
        redis.delete(key);
        RedisBitmap redisBitmap = redis.Bitmap(key);
        for (Long authorityId : authorityIds) {
            redisBitmap.set(authorityId, true);
        }
    }

    @Override
    public Boolean has(Long roleId, Long authorityId) {
        String key = getKey(roleId);
        RedisBitmap redisBitmap = redis.Bitmap(key);
        return redisBitmap.get(authorityId);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        RedisLock redisLock = redis.Lock(getKey(null) + "_lock");
//        try {
//            if (redisLock.tryLock()) {
//                List<RoleAuthorityEntity> roleAuthorities = roleAuthorityMapper.selectList(new LambdaQueryWrapper<>());
//                if (CollectionUtils.isNotEmpty(roleAuthorities)) {
//                    Map<Long, Set<Long>> authorityIdsMap = new HashMap<>(64, 1f);
//                    for (RoleAuthorityEntity roleAuthority : roleAuthorities) {
//                        Long roleId = roleAuthority.getRoleId();
//                        Set<Long> authorityIds = authorityIdsMap.get(roleId);
//                        if (authorityIds == null) {
//                            authorityIds = new HashSet<>(8, 1f);
//                            authorityIdsMap.put(roleId, authorityIds);
//                        }
//                        authorityIds.add(roleAuthority.getAuthorityId());
//                    }
//
//                    for (Map.Entry<Long, Set<Long>> entry : authorityIdsMap.entrySet()) {
//                        Long roleId = entry.getKey();
//                        Set<Long> authorityIds = entry.getValue();
//                        if (CollectionUtils.isNotEmpty(authorityIds)) {
//                            add(roleId, authorityIds);
//                        }
//                    }
//                }
//            }
//        } finally {
//            redisLock.forceUnlock();
//        }
    }

    private String getKey(Long roleId) {
        return "biz_sys_role_" + roleId + "_authorities";
    }

}
