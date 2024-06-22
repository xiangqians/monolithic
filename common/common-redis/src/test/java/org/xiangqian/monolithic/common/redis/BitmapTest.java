//package org.xiangqian.monolithic.common.redis;
//
//import org.junit.Test;
//
///**
// * @author xiangqian
// * @date 17:40 2024/06/22
// */
//public class BitmapTest {
//
//    @Test
//    public void testBitmap() {
//        Redis.Bitmap bitmap = redis.Bitmap("bitmap");
//        Boolean result = bitmap.setBit(10123, true);
//        System.out.println(result);
//
//        result = bitmap.getBit(100);
//        System.out.println(result);
//
//        result = bitmap.getBit(200);
//        System.out.println(result);
//
//        System.out.format("bitPos: %s", bitmap.bitPos(true)).println();
//        System.out.format("bitPos: %s", bitmap.bitPos(false)).println();
//        System.out.format("bitCount: %s", bitmap.bitCount()).println();
//        System.out.format("bitCountSum: %s", bitmap.bitCountSum("bitmap", "bitmap1")).println();
//    }
//
//
//}
