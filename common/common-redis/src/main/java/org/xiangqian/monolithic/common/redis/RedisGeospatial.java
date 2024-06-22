package org.xiangqian.monolithic.common.redis;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * Redis数据类型之Geospatial（地理位置）：存储地理位置坐标，并支持距离计算和范围查找。
 * <p>
 * 使用场景：同城的人、同城的店等
 * 应用场景：如位置服务、地理位置推荐等
 *
 * @author xiangqian
 * @date 17:23 2024/06/22
 */
public class RedisGeospatial {

    private GeoOperations<String, Object> geoOperations;
    private String key;

    RedisGeospatial(RedisTemplate<String, Object> redisTemplate, String key) {
        this.geoOperations = redisTemplate.opsForGeo();
        this.key = key;
    }

    /**
     * 添加地理位置数据
     *
     * @param x
     * @param y
     * @param member
     */
    public void add(double x, double y, Object member) {
        geoOperations.add(key, new Point(x, y), member);
    }

    /**
     * 获取地理位置数据的经纬度
     *
     * @param members
     * @return
     */
    public List<Point> position(Object... members) {
        return geoOperations.position(key, members);
    }

    /**
     * 计算两个地理位置之间的距离
     *
     * @param member1
     * @param member2
     * @param metric  度量单位，用于在地理空间查询中指定距离的度量方式
     *                {@link org.springframework.data.redis.domain.geo.Metrics#METERS} 米（Meter），这是国际单位制 (SI) 中的基本长度单位。
     *                {@link org.springframework.data.redis.domain.geo.Metrics#KILOMETERS} 千米（Kilometers）。1千米等于1000米。
     *                {@link org.springframework.data.redis.domain.geo.Metrics#MILES} 英里（Miles），主要在美国、英国等使用的非公制单位。1英里约等于1.60934千米。
     *                {@link org.springframework.data.redis.domain.geo.Metrics#FEET} 英尺（Feet），在非公制国家常用，1英尺等于12英寸或0.3048米.
     * @return
     */
    public Distance distance(Object member1, Object member2, Metric metric) {
        return geoOperations.distance(key, member1, member2, metric);
    }

    /**
     * 根据给定的中心点和半径范围进行搜索
     *
     * @param centerX
     * @param centerY
     * @param radius
     * @param metric
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> radius(double centerX, double centerY, double radius, Metric metric) {
        return geoOperations.radius(key, new Circle(new Point(centerX, centerY), new Distance(radius, metric)),
                // 通过args参数，可以设置诸如排序、限制返回结果数量、包括距离信息等附加信息。
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        // 包含距离信息
                        .includeDistance()
                        // 距离升序
                        .sortAscending());
    }

    /**
     * 根据给定的成员作为中心点和半径范围进行搜索
     *
     * @param member
     * @param radius
     * @param metric
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> radius(Object member, double radius, Metric metric) {
        return geoOperations.radius(key, member, new Distance(radius, metric),
                // 通过args参数，可以设置诸如排序、限制返回结果数量、包括距离信息等附加信息。
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        // 包含距离信息
                        .includeDistance()
                        // 距离升序
                        .sortAscending());
    }

}
