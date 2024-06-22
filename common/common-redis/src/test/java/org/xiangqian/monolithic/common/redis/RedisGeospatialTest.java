//package org.xiangqian.monolithic.common.redis;
//
//import org.junit.Test;
//import org.springframework.data.geo.Distance;
//import org.springframework.data.geo.GeoResult;
//import org.springframework.data.geo.GeoResults;
//import org.springframework.data.geo.Point;
//import org.springframework.data.redis.connection.RedisGeoCommands;
//import org.springframework.data.redis.domain.geo.Metrics;
//
//import java.util.List;
//
///**
// * @author xiangqian
// * @date 17:39 2024/06/22
// */
//public class RedisGeospatialTest {
//
//
//    @Test
//    public void testGeospatialRadius() {
//        Redis.Geospatial geospatial = redis.Geospatial("locations");
//        String shanghai = "Shanghai";
//        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = geospatial.radius(shanghai, 1300, Metrics.KILOMETERS);
//        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult : geoResults) {
//            RedisGeoCommands.GeoLocation<Object> geoLocation = geoResult.getContent();
//            Distance distance = geoResult.getDistance();
//            System.out.format("Member: %s, Distance: %s %s", geoLocation.getName(), distance.getValue(), distance.getUnit()).println();
//        }
//    }
//
//    @Test
//    public void testGeospatialDistance() {
//        Redis.Geospatial geospatial = redis.Geospatial("locations");
//        String beijing = "Beijing";
//        String shanghai = "Shanghai";
//        Distance distance = geospatial.distance(beijing, shanghai, Metrics.KILOMETERS);
//        System.out.format("Distance between Beijing and Shanghai: %s %s", distance.getValue(), distance.getUnit()).println();
//    }
//
//    @Test
//    public void testGeospatialPosition() {
//        Redis.Geospatial geospatial = redis.Geospatial("locations");
//
//        String shenzhen = "Shenzhen";
//        List<Point> points = geospatial.position(shenzhen);
//        for (Point point : points) {
//            System.out.format("Lon = %s, Lat = %s", point.getX(), point.getY()).println();
//        }
//    }
//
//    @Test
//    public void testGeospatialAdd() {
//        Redis.Geospatial geospatial = redis.Geospatial("locations");
//
//        // 经度
//        double lon = 116.36493983963013;
//        // 纬度
//        double lat = 40.10271704198785;
//        String beijing = "Beijing";
//        geospatial.add(lon, lat, beijing);
//
//        lon = 121.48941;
//        lat = 31.40527;
//        String shanghai = "Shanghai";
//        geospatial.add(lon, lat, shanghai);
//
//        lon = 113.88308;
//        lat = 22.55329;
//        String shenzhen = "Shenzhen";
//        geospatial.add(lon, lat, shenzhen);
//    }
//
//}
