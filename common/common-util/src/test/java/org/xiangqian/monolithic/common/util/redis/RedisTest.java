package org.xiangqian.monolithic.common.util.redis;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.domain.geo.Metrics;
import org.xiangqian.monolithic.common.util.DateTimeUtil;
import org.xiangqian.monolithic.common.util.Redis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 13:22 2024/06/01
 */
public class RedisTest {

    private Redis redis;


    @Test
    @SneakyThrows
    public void testPubSub() {
        String channel = "test-channel";
        redis.subscribe((message, pattern) -> {
            System.out.format("【订阅者1】%s, %s", pattern != null ? new String(pattern) : null, message).println();
        }, channel);

        redis.pSubscribe((message, pattern) -> {
            System.out.format("【订阅者2】%s, %s", pattern != null ? new String(pattern) : null, message).println();
        }, "test-*");

        while (true) {
            redis.publish(channel, String.format("Hello, %s", DateTimeUtil.format(LocalDateTime.now())));
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testGeospatialRadius() {
        Redis.Geospatial geospatial = redis.Geospatial("locations");
        String shanghai = "Shanghai";
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = geospatial.radius(shanghai, 1300, Metrics.KILOMETERS);
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult : geoResults) {
            RedisGeoCommands.GeoLocation<Object> geoLocation = geoResult.getContent();
            Distance distance = geoResult.getDistance();
            System.out.format("Member: %s, Distance: %s %s", geoLocation.getName(), distance.getValue(), distance.getUnit()).println();
        }
    }

    @Test
    public void testGeospatialDistance() {
        Redis.Geospatial geospatial = redis.Geospatial("locations");
        String beijing = "Beijing";
        String shanghai = "Shanghai";
        Distance distance = geospatial.distance(beijing, shanghai, Metrics.KILOMETERS);
        System.out.format("Distance between Beijing and Shanghai: %s %s", distance.getValue(), distance.getUnit()).println();
    }

    @Test
    public void testGeospatialPosition() {
        Redis.Geospatial geospatial = redis.Geospatial("locations");

        String shenzhen = "Shenzhen";
        List<Point> points = geospatial.position(shenzhen);
        for (Point point : points) {
            System.out.format("Lon = %s, Lat = %s", point.getX(), point.getY()).println();
        }
    }

    @Test
    public void testGeospatialAdd() {
        Redis.Geospatial geospatial = redis.Geospatial("locations");

        // 经度
        double lon = 116.36493983963013;
        // 纬度
        double lat = 40.10271704198785;
        String beijing = "Beijing";
        geospatial.add(lon, lat, beijing);

        lon = 121.48941;
        lat = 31.40527;
        String shanghai = "Shanghai";
        geospatial.add(lon, lat, shanghai);

        lon = 113.88308;
        lat = 22.55329;
        String shenzhen = "Shenzhen";
        geospatial.add(lon, lat, shenzhen);
    }

    @Test
    public void testHyperLogLog() {
        Redis.HyperLogLog hyperLogLog = redis.HyperLogLog("hyperLogLog");

        // 添加元素
        hyperLogLog.add("element1", "element2", "element3");
        hyperLogLog.add("element4", "element5");

        // 获取基数估算值
        Long size = hyperLogLog.size();
        System.out.format("Estimated cardinality: %s", size).println();
    }

    @Test
    public void testBitmap() {
        Redis.Bitmap bitmap = redis.Bitmap("bitmap");
        Boolean result = bitmap.setBit(10123, true);
        System.out.println(result);

        result = bitmap.getBit(100);
        System.out.println(result);

        result = bitmap.getBit(200);
        System.out.println(result);

        System.out.format("bitPos: %s", bitmap.bitPos(true)).println();
        System.out.format("bitPos: %s", bitmap.bitPos(false)).println();
        System.out.format("bitCount: %s", bitmap.bitCount()).println();
        System.out.format("bitCountSum: %s", bitmap.bitCountSum("bitmap", "bitmap1")).println();
    }

    @Before
    public final void before() {
        redis = Redis.create();
    }

}
