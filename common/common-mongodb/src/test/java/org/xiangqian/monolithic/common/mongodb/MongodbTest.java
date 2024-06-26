package org.xiangqian.monolithic.common.mongodb;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author xiangqian
 * @date 21:21 2024/06/25
 */
public class MongodbTest {

    private Mongodb mongodb;

    @Before
    public void before() {
        mongodb = Mongodb.create("test");
    }

    @After
    public void after() {
        IOUtils.closeQuietly(mongodb);
    }

    @Test
    public void testGetCollectionNames() {
        List<String> collectionNames = mongodb.getCollectionNames();
        System.out.println(collectionNames);
    }

    @Test
    public void testAddDocument1() {
        Document document = new Document("name", "Alice").append("age", 25).append("city", "New York");
        InsertOneResult insertOneResult = mongodb.addDocument("collection-1", document);
        System.out.println(insertOneResult);
    }

    @Test
    @SneakyThrows
    public void testAddDocument2() {
        Document document = new Document("name", "Alice").append("age", 25).append("city", "New York")
                // 将二进制数据存储为普通文档的一部分
                .append("file", new Binary(FileUtils.readFileToByteArray(new File("E:\\tmp\\navicat.pdf"))));
        InsertOneResult insertOneResult = mongodb.addDocument("collection-1", document);
        System.out.println(insertOneResult);
    }

    @Test
    public void testAddDocuments() {
        InsertManyResult insertManyResult = mongodb.addDocuments("collection-2",
                List.of(new Document("name", "Alice").append("age", 25).append("city", "New York"),
                        new Document("name", "Bob").append("age", 30).append("city", "San Francisco"),
                        new Document("name", "Charlie").append("age", 35).append("city", "Los Angeles")));
        System.out.println(insertManyResult);
    }

    @Test
    public void testGetDocuments1() {
        Bson filter = new Document("name", "Alice");
        List<Document> documents = mongodb.getDocuments("collection-2", filter);
        for (Document document : documents) {
            System.out.println(document);
            System.out.println(document.toJson());
            System.out.println();
        }
    }

    @Test
    public void testGetDocuments2() {
        Bson filter = Filters.in("age", 25, 30);
        List<Document> documents = mongodb.getDocuments("collection-2", filter);
        for (Document document : documents) {
            System.out.println(document);
            System.out.println(document.toJson());
            System.out.println();
        }
    }

    @Test
    public void testGetDocumentById() {
        Document document = mongodb.getDocumentById("collection-2", "667a33f1349b4437fbfbab09");
        System.out.println(document);
        if (document != null) {
            System.out.println(document.toJson());
        }
    }

    @Test
    public void testGetDocumentsByIds() {
        List<Document> documents = mongodb.getDocumentsByIds("collection-2", Set.of("667a33f1349b4437fbfbab09", "667a33f1349b4437fbfbab0a"));
        for (Document document : documents) {
            System.out.println(document);
            System.out.println(document.toJson());
            System.out.println();
        }
    }

    @Test
    public void testUpdDocument() {
        Bson filter = new Document("name", "Alice");
        UpdateResult updateResult = mongodb.updDocument("collection-2", filter, new Document("name", "Alice").append("age", 26).append("city", "New York"));
        System.out.println(updateResult);
    }

    @Test
    public void testDelDocument() {
        Bson filter = new Document("name", "Alice");
        DeleteResult deleteResult = mongodb.delDocument("collection-2", filter);
        System.out.println(deleteResult);
    }

    @Test
    @SneakyThrows
    public void testUploadFile() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("E:\\tmp\\navicat.pdf");
            ObjectId objectId = mongodb.uploadFile("navicat.pdf", inputStream);
            System.out.println(objectId);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test
    public void testGetFileById() {
        GridFSFile gridFSFile = mongodb.getFileById("667a4d0669a8d47da6b8f7f1");
        System.out.println(gridFSFile);
    }

    @Test
    public void testGetIndexes() {
        List<Document> indexes = mongodb.getIndexes("collection-2");
        indexes.forEach(System.out::println);
    }

    // 聚合框架
    @Test
    public void test() {
        MongoCollection<Document> collection = null;

        // 聚合管道
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$match", new Document("age", new Document("$gte", 25))),
                new Document("$group", new Document("_id", "$city")
                        .append("averageAge", new Document("$avg", "$age")))
        ));

        for (Document doc : result) {
            System.out.println(doc.toJson());
        }
    }

}
