package org.xiangqian.monolithic.doc;

import lombok.AllArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link GroupedOpenApi} 工厂bean
 *
 * @author xiangqian
 * @date 21:19 2023/05/12
 */
@AllArgsConstructor
public class GroupedOpenApiFactoryBean implements FactoryBean<GroupedOpenApi> {

    private String group;
    private String[] pkgs;

    @Override
    public GroupedOpenApi getObject() throws Exception {
        return DocConfiguration.buildGroupedOpenApi(group, pkgs);
    }

    @Override
    public Class<?> getObjectType() {
        return GroupedOpenApi.class;
    }

}
