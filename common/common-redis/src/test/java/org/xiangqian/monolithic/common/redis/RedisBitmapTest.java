package org.xiangqian.monolithic.common.redis;

import org.junit.Before;
import org.junit.Test;

/**
 * @author xiangqian
 * @date 17:40 2024/06/22
 */
public class RedisBitmapTest {

    private Redis redis;

    @Before
    public void before() {
        redis = Redis.create();
    }

    @Test
    public void testSet() {
        RedisBitmap redisBitmap = redis.Bitmap("test-bitmap");
        Boolean result = redisBitmap.set(100, true);
        System.out.println(result);
    }

    @Test
    public void testGet() {
        RedisBitmap redisBitmap = redis.Bitmap("test-bitmap");
        Boolean result = redisBitmap.get(100);
        System.out.println(result);

        result = redisBitmap.get(101);
        System.out.println(result);
    }

    @Test
    public void testPos() {
        RedisBitmap redisBitmap = redis.Bitmap("test-bitmap");
        Long offset = redisBitmap.pos(true);
        System.out.println(offset);

        offset = redisBitmap.pos(false);
        System.out.println(offset);
    }

    @Test
    public void testCount() {
        RedisBitmap redisBitmap = redis.Bitmap("test-bitmap");
        Long count = redisBitmap.count();
        System.out.println(count); // 1

        // 统计前 16 个比特（即 2 个字节）的位数为 1 的数量
        count = redisBitmap.count(0, 1);
        System.out.println(count); // 0

        // 统计前 96 个比特（即 12 个字节）的位数为 1 的数量
        count = redisBitmap.count(0, 11);
        System.out.println(count); // 0

        // 统计前 104 个比特（即 13 个字节）的位数为 1 的数量
        count = redisBitmap.count(0, 12);
        System.out.println(count); // 1
    }

}
