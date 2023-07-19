package org.xiangqian.monolithic.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:02 2020/11/09
 */
public class JsonUtil {

    private static final ObjectMapper om = new ObjectMapper();

    static {
        // 序列化包含设置
//        om.setSerializationInclusion(JsonInclude.Include.ALWAYS); // 默认
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 属性为NULL不序列化

        // 查找和注册模块
//        om.findAndRegisterModules();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        String dateTimePattern = "yyyy/MM/dd HH:mm:ss";
        String datePattern = "yyyy/MM/dd";
        String timePattern = "HH:mm:ss";

        // LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimePattern)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimePattern)));

        // LocalDate
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(datePattern)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(datePattern)));

        // LocalTime
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(timePattern)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timePattern)));

        om.registerModule(javaTimeModule);
    }

    public static ObjectNode createObjectNode() {
        return om.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return om.createArrayNode();
    }

    public static String serializeAsStr(Object obj) throws IOException {
        return serializeAsStr(obj, false);
    }

    /**
     * 序列化为字符串
     *
     * @param obj
     * @param pretty 漂亮的格式化
     * @return
     * @throws IOException
     */
    public static String serializeAsStr(Object obj, boolean pretty) throws IOException {
        if (pretty) {
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }

        return om.writeValueAsString(obj);
    }

    /**
     * 序列化为字节数组
     *
     * @param o
     * @return
     * @throws IOException
     */
    public static byte[] serializeAsBytes(Object o) throws IOException {
        return om.writeValueAsBytes(o);
    }

    public static <T> T deserialize(String text, Class<T> type) throws IOException {
        return om.readValue(text, type);
    }

    public static <T> T deserialize(String text, TypeReference<T> typeRef) throws IOException {
        return om.readValue(text, typeRef);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return om.readValue(bytes, type);
    }

    public static <T> T deserialize(byte[] bytes, TypeReference<T> typeRef) throws IOException {
        return om.readValue(bytes, typeRef);
    }

    public static <T> T deserialize(Map map, Class<T> type) throws IOException {
        return om.convertValue(map, type);
    }

    public static <T> T deserialize(Map map, TypeReference<T> typeRef) throws IOException {
        return om.convertValue(map, typeRef);
    }

    public static <T> T deserialize(List list, Class<T> type) throws IOException {
        return om.convertValue(list, type);
    }

    public static <T> T deserialize(List list, TypeReference<T> typeRef) throws IOException {
        return om.convertValue(list, typeRef);
    }

    /**
     * 反序列化为 {@link JsonNode}
     *
     * @param text
     * @return
     * @throws IOException
     */
    public static JsonNode deserializeAsJsonNode(String text) throws IOException {
        return om.readTree(text);
    }

}
