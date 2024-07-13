package org.xiangqian.monolithic.common.mysql;

import org.junit.Test;
import org.xiangqian.monolithic.common.generator.XmlGenerator;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * @author xiangqian
 * @date 19:00 2024/01/29
 */
public class XmlGeneratorTest {

    @Test
    public void select() {
        XmlGenerator.select(UserEntity.class, "u", "entity");
    }

    @Test
    public void update() {
        XmlGenerator.update(UserEntity.class, null);
    }

    @Test
    public void insert() {
        XmlGenerator.insert(UserEntity.class, "entity");
    }

}
