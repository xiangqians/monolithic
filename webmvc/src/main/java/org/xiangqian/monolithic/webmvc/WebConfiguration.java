package org.xiangqian.monolithic.webmvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;
import org.xiangqian.monolithic.common.util.time.DateUtil;
import org.xiangqian.monolithic.common.util.time.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author xiangqian
 * @date 21:15 2024/06/02
 */
@Configuration(proxyBeanMethods = false)
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private MethodHandler methodHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(methodHandler).addPathPatterns("/api/**");
    }

    // 配置knife4j文档
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // 解决 spring.jackson.date-format 配置不生效问题
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = new ObjectMapper();

                // 属性为NULL不序列化
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                // 忽略未知属性
                // 当禁用反序列化时遇到未知属性报错时，Jackson 默认情况下要求 JSON 字符串中的所有属性都要与 Java 类的字段完全匹配，如果 JSON 中包含了未知属性，就会抛出异常
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

                objectMapper.registerModule(javaTimeModule);

                mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
            }
        }
    }

}
