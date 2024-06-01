//package org.xiangqian.monolithic.webmvc.generator;
//
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import org.apache.commons.lang3.StringUtils;
//import org.xiangqian.microservices.common.util.NamingUtil;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author xiangqian
// * @date 19:00 2024/01/29
// */
//public class MapperCodeGenerator {
//
//    public static String insert(Class<?> clazz) {
//        Field[] fields = clazz.getDeclaredFields();
//        List<String> columns = new ArrayList<>(fields.length);
//        List<String> values = new ArrayList<>(fields.length);
//        for (Field field : fields) {
//            int modifiers = field.getModifiers();
//            TableField tableFieldAnnotation = null;
//            if (Modifier.isStatic(modifiers)
//                    || Modifier.isFinal(modifiers)
//                    || ((tableFieldAnnotation = field.getAnnotation(TableField.class)) != null && !tableFieldAnnotation.exist())) {
//                continue;
//            }
//
//            String name = field.getName();
//            columns.add(String.format("<if test=\"%s != null\">%s,</if>", name, NamingUtil.LowerCamel.convToLowerUnderscore(name)));
//            values.add(String.format("<if test=\"%s != null\">#{%s},</if>", name, name));
//        }
//
//        StringBuilder xmlBuilder = new StringBuilder();
//        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
//        xmlBuilder.append(String.format("INSERT INTO %s", tableNameAnnotation.value()));
//
//        xmlBuilder.append("\n");
//        xmlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
//        xmlBuilder.append("\n\t");
//        xmlBuilder.append(StringUtils.join(columns, "\n\t"));
//        xmlBuilder.append("\n");
//        xmlBuilder.append("</trim>");
//
//        xmlBuilder.append("\n");
//        xmlBuilder.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
//        xmlBuilder.append("\n\t");
//        xmlBuilder.append(StringUtils.join(values, "\n\t"));
//        xmlBuilder.append("\n");
//        xmlBuilder.append("</trim>");
//
//        return xmlBuilder.toString();
//    }
//
//}
