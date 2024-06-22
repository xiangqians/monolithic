package org.xiangqian.monolithic.common.redis;

/**
 * Bloom filter
 * Bloom filters are a probabilistic data structure that checks for presence of an element in a set.
 * <p>
 * Redis 提供了 Bloom filter 的实现作为其插件之一（通过 RedisBloom 模块），可以直接在 Redis 服务器上使用，因此不需要单独安装。你只需确保 Redis 服务器运行并加载了 RedisBloom 模块。
 *
 * @author xiangqian
 * @date 17:30 2024/06/22
 */
public class RedisBloomFilter implements Probabilistic {
}
