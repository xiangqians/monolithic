//package org.xiangqian.monolithic.common.redis;
//
//import org.junit.Test;
//
///**
// * @author xiangqian
// * @date 17:40 2024/06/22
// */
//public class HyperLogLogTest {
//
//
//    @Test
//    public void testHyperLogLog() {
//        Redis.HyperLogLog hyperLogLog = redis.HyperLogLog("hyperLogLog");
//
//        // 添加元素
//        hyperLogLog.add("element1", "element2", "element3");
//        hyperLogLog.add("element4", "element5");
//
//        // 获取基数估算值
//        Long size = hyperLogLog.size();
//        System.out.format("Estimated cardinality: %s", size).println();
//    }
//
//
//}
