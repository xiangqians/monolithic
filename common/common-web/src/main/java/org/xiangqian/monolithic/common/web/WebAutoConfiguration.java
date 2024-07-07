package org.xiangqian.monolithic.common.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityGroupMapper;
import org.xiangqian.monolithic.common.mysql.sys.mapper.AuthorityMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 11:19 2024/07/07
 */
@Configuration(proxyBeanMethods = false)
public class WebAutoConfiguration {

    @Bean
    public WebMethodAuthority webMethodAuthority() {
        return new WebMethodAuthority();
    }

    @Bean
    public Map<Method, AuthorityEntity> methodAuthorityMap(List<AuthorityGroupEntity> authorityGroups,
                                                           AuthorityGroupMapper authorityGroupMapper,
                                                           AuthorityMapper authorityMapper) {
        int size = authorityGroups.size();
        List<Long> authorityGroupIds = new ArrayList<>(size);
        List<Long> authorityIds = new ArrayList<>(size);
        for (AuthorityGroupEntity authorityGroup : authorityGroups) {
            AuthorityGroupEntity queryAuthorityGroup = new AuthorityGroupEntity();
            queryAuthorityGroup.setPath(authorityGroup.getPath());
            AuthorityGroupEntity storedAuthorityGroup = authorityGroupMapper.getOne(queryAuthorityGroup);
            if (storedAuthorityGroup == null) {
                authorityGroupMapper.insert(authorityGroup);
            } else {
                authorityGroup.setId(storedAuthorityGroup.getId());
                if (!storedAuthorityGroup.getRem().equals(authorityGroup.getRem()) || storedAuthorityGroup.getDel() == 1) {
                    authorityGroupMapper.updById(authorityGroup);
                }
            }
            Long authorityGroupId = authorityGroup.getId();
            authorityGroupIds.add(authorityGroupId);

            for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                authority.setGroupId(authorityGroupId);

                AuthorityEntity queryAuthority = new AuthorityEntity();
                queryAuthority.setMethod(authority.getMethod());
                queryAuthority.setPath(authority.getPath());
                AuthorityEntity storedAuthority = authorityMapper.getOne(queryAuthority);
                if (storedAuthority == null) {
                    authorityMapper.insert(authority);
                } else {
                    authority.setId(storedAuthority.getId());
                    if (!storedAuthority.getGroupId().equals(authority.getGroupId())
                            || !storedAuthority.getAllow().equals(authority.getAllow())
                            || !storedAuthority.getRem().equals(authority.getRem())
                            || storedAuthority.getDel() == 1) {
                        authorityMapper.updById(authority);
                    }
                }
                authorityIds.add(authority.getId());
            }
        }
        authorityGroupMapper.delete(new LambdaQueryWrapper<AuthorityGroupEntity>().notIn(AuthorityGroupEntity::getId, authorityGroupIds));
        authorityMapper.delete(new LambdaQueryWrapper<AuthorityEntity>().notIn(AuthorityEntity::getId, authorityIds));


        Map<Method, AuthorityEntity> methodAuthorityMap = new HashMap<>(authorityGroups.size() * 2, 1f);
        for (AuthorityGroupEntity authorityGroup : authorityGroups) {
            for (AuthorityEntity authority : authorityGroup.getAuthorities()) {
                methodAuthorityMap.put(authority.getHandleMethod(), authority);
            }
        }
        return methodAuthorityMap;
    }

}
