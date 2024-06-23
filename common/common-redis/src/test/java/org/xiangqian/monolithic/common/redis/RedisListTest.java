package org.xiangqian.monolithic.common.redis;

import org.junit.Before;

/**
 * @author xiangqian
 * @date 13:31 2024/06/23
 */
public class RedisListTest {

    private Redis redis;

    @Before
    public void before() {
        redis = Redis.create();
    }


}
