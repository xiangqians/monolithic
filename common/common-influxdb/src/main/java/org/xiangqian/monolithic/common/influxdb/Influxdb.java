package org.xiangqian.monolithic.common.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.Query;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:28 2024/06/24
 */
public class Influxdb implements Closeable {

    private InfluxDBClient influxDBClient;

    public Influxdb(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    /**
     * 使用 Point 对象写入数据
     *
     * @param measurementName 测量名称
     * @param tags
     * @param fields
     */
    public void writePoint(String measurementName, Map<String, String> tags, Map<String, Object> fields, Duration time) {
        Point point = Point.measurement(measurementName)
                .addTags(tags)
                .addFields(fields)
                .time(time.toNanos(), WritePrecision.NS);
        influxDBClient.getWriteApiBlocking().writePoint(point);
    }

    //使用行协议写入数据
    //java
    //public class WriteLineProtocolExample {
    //    public static void writeLineProtocol(InfluxDBClient client, String bucket, String org) {
    //        String data = "weather,location=us-midwest temperature=82 1465839830100400200";
    //        client.getWriteApiBlocking().writeRecord(bucket, org, WritePrecision.NS, data);
    //    }
    //}

    //查询数据
    //使用 Flux 查询语言来查询数据：
    public void query(String measurementName, String tagName, String tagValue, String fieldName, String fieldValue) {
        String fluxQuery = "from(bucket:\"my-bucket\") |> range(start: -1h)";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);

        influxDBClient.getQueryApi().query(Query.SERIALIZED_NAME_QUERY);

        for (FluxTable table : tables) {
            table.getRecords().forEach(record -> {
                System.out.println(record.getTime() + ": " + record.getValueByKey("value"));
            });
        }
    }


    //管理 Buckets
    //你也可以使用 BucketsApi 来管理 InfluxDB 中的 buckets。
    //
    //创建 Bucket
    //java
    //import com.influxdb.client.BucketsApi;
    //import com.influxdb.client.domain.Bucket;
    //import com.influxdb.client.domain.RetentionRule;
    //
    //import java.util.Arrays;
    //
    //public class CreateBucketExample {
    //    public static void createBucket(InfluxDBClient client, String orgId) {
    //        BucketsApi bucketsApi = client.getBucketsApi();
    //        RetentionRule retentionRule = new RetentionRule();
    //        retentionRule.setEverySeconds(3600); // Data retention period
    //
    //        Bucket bucket = bucketsApi.createBucket("my-new-bucket", retentionRule, orgId);
    //        System.out.println("Created bucket with ID: " + bucket.getId());
    //    }
    //}
    //删除 Bucket
    //java
    //public class DeleteBucketExample {
    //    public static void deleteBucket(InfluxDBClient client, String bucketId) {
    //        BucketsApi bucketsApi = client.getBucketsApi();
    //        bucketsApi.deleteBucket(bucketId);
    //        System.out.println("Deleted bucket with ID: " + bucketId);
    //    }
    //}
    //完整示例
    //将上述代码片段结合在一起，我们可以创建一个完整的应用程序示例：
    //
    //java
    //import com.influxdb.client.InfluxDBClient;
    //import com.influxdb.client.InfluxDBClientFactory;
    //import com.influxdb.client.domain.WritePrecision;
    //import com.influxdb.client.write.Point;
    //import com.influxdb.query.FluxTable;
    //
    //import java.time.Instant;
    //import java.util.List;
    //
    //public class InfluxDBClientExample {
    //    public static void main(String[] args) {
    //        String url = "http://localhost:8086";
    //        String token = "my-token";
    //        String org = "my-org";
    //        String bucket = "my-bucket";
    //
    //        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    //
    //        try {
    //            // Write data
    //            Point point = Point.measurement("temperature")
    //                    .addTag("location", "office")
    //                    .addField("value", 23.5)
    //                    .time(Instant.now(), WritePrecision.MS);
    //            client.getWriteApiBlocking().writePoint(bucket, org, point);
    //
    //            // Query data
    //            String fluxQuery = "from(bucket:\"my-bucket\") |> range(start: -1h)";
    //            List<FluxTable> tables = client.getQueryApi().query(fluxQuery, org);
    //            for (FluxTable table : tables) {
    //                table.getRecords().forEach(record -> {
    //                    System.out.println(record.getTime() + ": " + record.getValueByKey("value"));
    //                });
    //            }
    //        } finally {
    //            client.close();
    //        }
    //    }
    //}

    @Override
    public void close() throws IOException {
        influxDBClient.close();
    }

    public static Influxdb create(String url, String token, String org, String bucket) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(InfluxDBClientOptions.builder()
                .url(url)
                .authenticateToken(token.toCharArray())
                .org(org)
                .bucket(bucket)
                .build());
        return new Influxdb(influxDBClient);
    }

}
