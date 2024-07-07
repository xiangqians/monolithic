package org.xiangqian.monolithic.common.minio;

import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * @author xiangqian
 * @date 19:35 2024/06/14
 */
public class MinioTest {

    private Minio minio;

    private final String bucket = "test2";

    @Test
    public void testRemoveObject() throws Exception {
        minio.removeObject(bucket, "58a734ba2d2092090c72af14cd3f3cb0");
    }

    @Test
    public void testGetPresignedObjectUrl() throws Exception {
        String presignedObjectUrl = minio.getPresignedObjectUrl(bucket, "58a734ba2d2092090c72af14cd3f3cb0", Duration.ofSeconds(30));
        System.out.println(presignedObjectUrl);
    }

    @Test
    public void testGetObject() throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = minio.getObject(bucket, "58a734ba2d2092090c72af14cd3f3cb0");
            outputStream = new FileOutputStream("E:\\tmp\\image\\58a734ba2d2092090c72af14cd3f3cb0.png");
            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream, outputStream);
        }
    }

    @Test
    public void testStatObject() throws Exception {
        StatObjectResponse statObject = minio.statObject(bucket, "58a734ba2d2092090c72af14cd3f3cb0");
        System.out.format("Bucket Name\t\t: %s", statObject.bucket()).println();
        System.out.format("Object Name\t\t: %s", statObject.object()).println();
        System.out.format("Size\t\t\t: %s Byte", statObject.size()).println();
        System.out.format("Last Modified\t: %s", Optional.ofNullable(statObject.lastModified()).map(DateTimeUtil::ofZonedDateTime).map(DateTimeUtil::format).orElse(null)).println();
        System.out.format("ETag\t\t\t: %s", statObject.etag()).println();
        System.out.format("Content Type\t: %s", statObject.contentType()).println();
    }

    @Test
    public void testPutObject() throws Exception {
        File file = new File("E:\\tmp\\image\\D5DD01A6-3A4A-425C-A51A-A9A08907A45B.png");
//        file = new File("E:\\tmp\\image\\test.png");
        minio.putObject(bucket, file);
    }

    @Test
    public void testListObjects() throws Exception {
        Iterable<Result<Item>> iterable = minio.listObjects(bucket);
        for (Result<Item> result : iterable) {
            Item item = result.get();
            System.out.format("Object Name\t\t: %s", item.objectName()).println();
            if (!item.isDir()) {
                System.out.format("Size\t\t\t: %s Byte", item.size()).println();
                System.out.format("Last Modified\t: %s", Optional.ofNullable(item.lastModified()).map(DateTimeUtil::ofZonedDateTime).map(DateTimeUtil::format).orElse(null)).println();
                System.out.format("ETag\t\t\t: %s", item.etag()).println();
            }
            System.out.println();
        }
    }

    @Test
    public void testRemoveBucket() throws Exception {
        minio.removeBucket(bucket);
    }

    @Test
    public void testMakeBucket() throws Exception {
        minio.makeBucket(bucket);
    }

    @Test
    public void testListBuckets() throws Exception {
        List<Bucket> buckets = minio.listBuckets();
        if (CollectionUtils.isNotEmpty(buckets)) {
            for (Bucket bucket : buckets) {
                System.out.format("%s\t%s", bucket.name(), DateTimeUtil.format(DateTimeUtil.ofZonedDateTime(bucket.creationDate()))).println();
            }
        }
    }

    @Test
    public void testBucketExists() throws Exception {
        System.out.println(minio.bucketExists("test"));
        System.out.println(minio.bucketExists("test1"));
        System.out.println(minio.bucketExists("test2"));
    }

    @Before
    public void before() {
        minio = new Minio("http://localhost:9000", "m0dWHllxoipmIczh", "Kr2Uq0tCHr0HjLLBHBiYzDs5tznle4jv") {
            private final String prefix = "tmp/";

            @Override
            public Iterable<Result<Item>> listObjects(String bucket, String prefix, boolean recursive) {
                prefix = this.prefix;
                return super.listObjects(bucket, prefix, recursive);
            }

            @Override
            public void putObject(String bucket, String name, InputStream stream, String contentType) throws Exception {
                name = prefix + name;
                super.putObject(bucket, name, stream, contentType);
            }

            @Override
            public StatObjectResponse statObject(String bucket, String name) throws Exception {
                name = prefix + name;
                return super.statObject(bucket, name);
            }

            @Override
            public InputStream getObject(String bucket, String name) throws Exception {
                name = prefix + name;
                return super.getObject(bucket, name);
            }

            @Override
            public String getPresignedObjectUrl(String bucket, String name, Duration expiry) throws Exception {
                name = prefix + name;
                return super.getPresignedObjectUrl(bucket, name, expiry);
            }

            @Override
            public void removeObject(String bucket, String name) throws Exception {
                name = prefix + name;
                super.removeObject(bucket, name);
            }
        };
    }

}
