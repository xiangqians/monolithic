package org.xiangqian.monolithic.util;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * @author xiangqian
 * @date 18:59 2024/06/14
 */
public class Minio {

    private MinioClient minioClient;

    public Minio(String endpoint, String accessKey, String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 查看存储桶是否存在
     *
     * @param bucket 存储桶
     * @return
     * @throws Exception
     */
    public boolean bucketExists(String bucket) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build());
    }

    /**
     * 获取全部存储桶
     *
     * @return
     * @throws Exception
     */
    public List<Bucket> listBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 创建存储桶
     *
     * @param bucket 存储桶
     * @throws Exception
     */
    public void makeBucket(String bucket) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucket)
                .build());
    }

    /**
     * 删除存储桶
     *
     * @param bucket 存储桶
     * @throws Exception
     */
    public void removeBucket(String bucket) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder()
                .bucket(bucket)
                .build());
    }

    /**
     * 获取存储桶下的所有对象
     *
     * @param bucket    存储桶
     * @param prefix    前缀，例如：directory/
     * @param recursive 是否递归查询
     * @return
     */
    public Iterable<Result<Item>> listObjects(String bucket, String prefix, boolean recursive) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .recursive(recursive)
                .build());
    }

    public Iterable<Result<Item>> listObjects(String bucket, boolean recursive) {
        return listObjects(bucket, null, recursive);
    }

    public Iterable<Result<Item>> listObjects(String bucket) {
        return listObjects(bucket, null, false);
    }

    /**
     * 上传对象
     *
     * @param bucket      存储桶
     * @param name        对象名称（对象名称相同会覆盖）
     * @param stream      对象流
     * @param contentType 对象流内容类型
     * @throws Exception
     */
    public void putObject(String bucket, String name, InputStream stream, String contentType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .stream(stream, stream.available(), -1)
                .contentType(contentType)
                .build());
    }

    public void putObject(String bucket, Path path) throws Exception {
        String name = Md5Util.encryptHex(path.getFileName().toString());
        String contentType = Files.probeContentType(path);
        InputStream stream = null;
        try {
            stream = Files.newInputStream(path);
            putObject(bucket, name, stream, contentType);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public void putObject(String bucket, File file) throws Exception {
        putObject(bucket, file.toPath());
    }

    public void putObject(String bucket, MultipartFile multipartFile) throws Exception {
        String name = Md5Util.encryptHex(multipartFile.getOriginalFilename());
        String contentType = multipartFile.getContentType();
        InputStream stream = null;
        try {
            stream = multipartFile.getInputStream();
            putObject(bucket, name, stream, contentType);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * 获取对象详情
     *
     * @param bucket 存储桶
     * @param name   对象名称
     * @return
     * @throws Exception
     */
    public StatObjectResponse statObject(String bucket, String name) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .build());
    }

    /**
     * 获取对象流
     *
     * @param bucket 存储桶
     * @param name   对象名称
     * @return
     * @throws Exception
     */
    public InputStream getObject(String bucket, String name) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .build());
    }

    /**
     * 获取预签名对象地址
     *
     * @param bucket 存储桶
     * @param name   对象名称
     * @param expiry 过期时间
     * @return
     * @throws Exception
     */
    public String getPresignedObjectUrl(String bucket, String name, Duration expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(name)
                .expiry((int) expiry.toSeconds()) // 单位：秒
                .method(Method.GET)
                .build());
    }

    /**
     * 删除对象
     *
     * @param bucket 存储桶
     * @param name   对象名称
     * @throws Exception
     */
    public void removeObject(String bucket, String name) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .build());
    }

}
