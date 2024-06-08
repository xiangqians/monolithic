package org.xiangqian.monolithic.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:02 2020/11/09
 */
public class JsonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 默认
//        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 属性为NULL不序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 忽略未知属性
        // 当禁用反序列化时遇到未知属性报错时，Jackson 默认情况下要求 JSON 字符串中的所有属性都要与 Java 类的字段完全匹配，如果 JSON 中包含了未知属性，就会抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 查找和注册模块
//        OBJECT_MAPPER.findAndRegisterModules();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.FORMATTER));

        // LocalDate
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateUtil.FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateUtil.FORMATTER));

        // LocalTime
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TimeUtil.FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TimeUtil.FORMATTER));

        OBJECT_MAPPER.registerModule(javaTimeModule);
    }

    public static String serializeAsString(Object object) throws IOException {
        return serializeAsString(object, false);
    }

    public static String serializeAsString(Object object, boolean indent) throws IOException {
        if (indent) {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static byte[] serializeAsBytes(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    public static <T> T deserialize(String string, Class<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(string, type);
    }

    public static <T> T deserialize(String string, TypeReference<T> typeRef) throws IOException {
        return OBJECT_MAPPER.readValue(string, typeRef);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(bytes, type);
    }

    public static <T> T deserialize(byte[] bytes, TypeReference<T> typeRef) throws IOException {
        return OBJECT_MAPPER.readValue(bytes, typeRef);
    }

    public static <T> T deserialize(Map map, Class<T> type) {
        return OBJECT_MAPPER.convertValue(map, type);
    }

    public static <T> T deserialize(Map map, TypeReference<T> typeRef) {
        return OBJECT_MAPPER.convertValue(map, typeRef);
    }

    public static <T> T deserialize(List list, Class<T> type) {
        return OBJECT_MAPPER.convertValue(list, type);
    }

    public static <T> T deserialize(List list, TypeReference<T> typeRef) {
        return OBJECT_MAPPER.convertValue(list, typeRef);
    }

}
