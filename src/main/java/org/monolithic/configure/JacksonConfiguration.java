package org.monolithic.configure;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson配置
 * <p>
 * 源码分析：
 * 创建ObjectMapper：
 * {@link org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration}
 * {@link org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperConfiguration#jacksonObjectMapper(org.springframework.http.converter.json.Jackson2ObjectMapperBuilder)}
 * <p>
 * 初始化Jackson2ObjectMapperBuilder
 * {@link  org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration#jacksonObjectMapperBuilder(org.springframework.context.ApplicationContext, java.util.List)}
 * {@link org.springframework.http.converter.json.Jackson2ObjectMapperBuilder} prototype原型模式，指的是每次调用时，会重新创建该类的一个实例，比较类似于我们自己自己new的对象实例。默认的singleton单实例模式
 * 根据源码分析，只需要将 {@link org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer} 实例注入IOC容器，即可实现自定义配置
 *
 * @author xiangqian
 * @date 11:29 2022/06/19
 */
@Configuration
public class JacksonConfiguration implements Jackson2ObjectMapperBuilderCustomizer {

    @Value("${spring.jackson.custom-data-time-format:yyyy-MM-dd HH:mm:ss}")
    private String customDataTimeFormat;

    @Value("${spring.jackson.custom-date-format:yyyy-MM-dd}")
    private String customDataFormat;

    @Value("${spring.jackson.custom-time-format:HH:mm:ss}")
    private String customTimeFormat;

    @Value("${spring.jackson.time-zone:GMT+8}")
    private String timeZone;

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        customizeDate(jacksonObjectMapperBuilder);
    }

    private void customizeDate(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        // timeZone
        TimeZone timeZone = TimeZone.getTimeZone(this.timeZone);

        // Date
        DateFormat df = new SimpleDateFormat(customDataTimeFormat);
        df.setTimeZone(timeZone);
        jacksonObjectMapperBuilder.dateFormat(df);

        // LocalDateTime
        jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(customDataTimeFormat)));
        jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(customDataTimeFormat)));

        // LocalDate
        jacksonObjectMapperBuilder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(customDataFormat)));
        jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(customDataFormat)));

        // LocalTime
        jacksonObjectMapperBuilder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(customTimeFormat)));
        jacksonObjectMapperBuilder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(customTimeFormat)));

        // TimeZone
        jacksonObjectMapperBuilder.timeZone(timeZone);
    }

}
