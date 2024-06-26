package org.xiangqian.monolithic.common.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 20:36 2024/06/25
 */
public class Mongodb implements Closeable {

    private MongoClient mongoClient;

    /**
     * 数据库
     */
    private MongoDatabase database;

    private Mongodb(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;

        // 获取数据库
        // 在插入数据时，如果数据库不存在则自动创建
        this.database = mongoClient.getDatabase(databaseName);
    }

    /**
     * 获取文档集合名称集
     *
     * @return
     */
    public List<String> getCollectionNames() {
        MongoIterable<String> iterable = database.listCollectionNames();
        MongoCursor<String> cursor = iterable.cursor();
        List<String> list = new ArrayList<>(cursor.available());
        while (cursor.hasNext()) {
            String collectionName = cursor.next();
            list.add(collectionName);
        }
        return list;
    }

    /**
     * 获取文档集合（用于新增、修改、查询、删除文档操作）
     * <p>
     * 在插入数据时，如果集合不存在则自动创建
     *
     * @param collectionName 文档集合名称
     * @return
     */
    private MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    /**
     * 创建文档
     *
     * @param collectionName 文档集合名称
     * @param document       文档
     * @return
     */
    public InsertOneResult addDocument(String collectionName, Document document) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.insertOne(document);
    }

    /**
     * 创建多个文档
     *
     * @param collectionName 文档集合名称
     * @param documents      多个文档
     * @return
     */
    public InsertManyResult addDocuments(String collectionName, List<Document> documents) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.insertMany(documents);
    }

    /**
     * 读取文档集合
     *
     * @param collectionName 文档集合名称
     * @param filter         过滤条件，{@link com.mongodb.client.model.Filters}
     * @return
     */
    public List<Document> getDocuments(String collectionName, Bson filter) {
        MongoCollection<Document> collection = getCollection(collectionName);
        FindIterable<Document> iterable = collection.find(filter);
        MongoCursor<Document> cursor = iterable.iterator();
        List<Document> list = new ArrayList<>(cursor.available());
        while (cursor.hasNext()) {
            Document document = cursor.next();
            list.add(document);
        }
        return list;
    }

    /**
     * 根据id读取文档
     *
     * @param collectionName 文档集合名称
     * @param id             文档id
     * @return
     */
    public Document getDocumentById(String collectionName, String id) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    /**
     * 根据id集合读取文档集合
     *
     * @param collectionName 文档集合名称
     * @param ids            文档id集合
     * @return
     */
    public List<Document> getDocumentsByIds(String collectionName, Set<String> ids) {
        return getDocuments(collectionName, Filters.in("_id", ids.stream().map(ObjectId::new).collect(Collectors.toList())));
    }

    /**
     * 更新文档
     *
     * @param collectionName 文档集合名称
     * @param filter         过滤条件，{@link com.mongodb.client.model.Filters}
     * @param document       要修改的文档
     * @return
     */
    public UpdateResult updDocument(String collectionName, Bson filter, Document document) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.updateOne(filter, new Document("$set", document));
    }

    /**
     * 根据id更新文档
     *
     * @param collectionName 文档集合名称
     * @param id             文档id
     * @param document       要修改的文档
     * @return
     */
    public UpdateResult updDocumentById(String collectionName, String id, Document document) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return updDocument(collectionName, Filters.eq("_id", new ObjectId(id)), document);
    }

    /**
     * 删除文档
     *
     * @param collectionName 文档集合名称
     * @param filter         过滤条件，{@link com.mongodb.client.model.Filters}
     * @return
     */
    public DeleteResult delDocument(String collectionName, Bson filter) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.deleteOne(filter);
    }

    /**
     * 根据id删除文档
     *
     * @param collectionName 文档集合名称
     * @param id             文档id
     * @return
     */
    public DeleteResult delDocumentById(String collectionName, String id) {
        return delDocument(collectionName, Filters.eq("_id", new ObjectId(id)));
    }

    /**
     * 根据id集合删除文档集合
     *
     * @param collectionName 文档集合名称
     * @param ids            文档id集合
     * @return
     */
    public DeleteResult delDocumentByIds(String collectionName, Set<String> ids) {
        return delDocument(collectionName, Filters.in("_id", ids.stream().map(ObjectId::new).collect(Collectors.toList())));
    }

    /**
     * MongoDB 上传二进制文件，主要可以通过以下两种方式：
     * 1、使用 GridFS
     * GridFS 是 MongoDB 提供的一种用于存储和检索大于 16MB 的文件的机制，适合存储大型文件，支持高效的分块存储和检索。
     * <p>
     * 优点：
     * 1、适合存储大型文件，提供了高效的分块存储和检索机制。
     * 2、支持高并发操作和文件的部分更新、下载等操作。
     * 3、可以方便地管理文件的元数据和版本。
     * <p>
     * <p>
     * 2、存储为普通文档的一部分
     * 对于小于 16MB 的二进制文件，也可以直接将其存储为 MongoDB 的普通文档的一部分。
     * 例如，可以将文件内容以二进制形式存储在一个字段中，同时可以存储其他元数据（如文件名、文件类型等）在其他字段中。
     * 这种方式相对简单，不需要使用 GridFS 的复杂性，适用于较小的二进制文件。
     * <p>
     * 优点：
     * 1）简单直接，适合小文件的存储需求。
     * 2）可以将文件和相关元数据一起存储在同一个文档中，方便查询和管理。
     *
     * @param name   文件名称
     * @param stream 文件数据流
     * @return
     */
    public ObjectId uploadFile(String name, InputStream stream) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        return gridFSBucket.uploadFromStream(name, stream);
    }

    /**
     * 根据 id 从 GridFS 获取文件
     *
     * @param id fs id
     */
    public GridFSFile getFileById(String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        return gridFSBucket.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    /**
     * 根据 id 从 GridFS 拷贝文件
     *
     * @param id fs id
     */
    public void copyFileById(String id, OutputStream outputStream) throws FileNotFoundException {
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(id))).first();
        if (gridFSFile == null) {
            throw new FileNotFoundException(id);
        }
        gridFSBucket.downloadToStream(gridFSFile.getId(), outputStream);
    }

    /**
     * 获取索引集合
     *
     * @param collectionName 文档集合名称
     * @return
     */
    public List<Document> getIndexes(String collectionName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        ListIndexesIterable<Document> iterable = collection.listIndexes();
        MongoCursor<Document> cursor = iterable.cursor();
        List<Document> list = new ArrayList<>(cursor.available());
        while (cursor.hasNext()) {
            Document index = cursor.next();
            list.add(index);
        }
        return list;
    }

    /**
     * 创建索引
     *
     * @param collectionName 文档集合名称
     * @param key            索引
     * @return
     */
    public String addIndex(String collectionName, Bson key) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.createIndex(key);
    }

    /**
     * 删除索引
     *
     * @param collectionName 文档集合名称
     * @param key            索引
     */
    public void delIndex(String collectionName, Bson key) {
        MongoCollection<Document> collection = getCollection(collectionName);
        collection.dropIndex(key);
    }

    /**
     * 删除索引
     *
     * @param collectionName 文档集合名称
     * @param key            索引
     */
    public void delIndex(String collectionName, String key) {
        MongoCollection<Document> collection = getCollection(collectionName);
        collection.dropIndex(key);
    }

    @Override
    public void close() throws IOException {
        if (mongoClient != null) {
            try {
                mongoClient.close();
            } finally {
                mongoClient = null;
                database = null;
            }
        }
    }

    public static Mongodb create(String databaseName) {
        return create("mongodb://localhost:27017", databaseName);
    }

    public static Mongodb create(String url, String databaseName) {
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(url))
                .build());
        return new Mongodb(mongoClient, databaseName);
    }

}
