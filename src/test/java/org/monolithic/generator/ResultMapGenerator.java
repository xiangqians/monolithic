package org.monolithic.generator;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author xiangqian
 * @date 21:48 2022/08/15
 */
public class ResultMapGenerator {

    public static void main(String[] args) {
        Class<?> clazz = Object.class;
        generate(clazz);
    }

    private static void generate(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();
        String simpleName = clazz.getSimpleName();
        builder.append(String.format("<resultMap id=\"%s%sMap\" type=\"%s\">", simpleName.substring(0, 1).toLowerCase(), simpleName.substring(1), clazz.getName())).append('\n');
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)
                    || Modifier.isStatic(modifiers)
                    || Modifier.isNative(modifiers)) {
                continue;
            }
            String fieldName = field.getName();
            builder.append('\t').append(String.format("<result column=\"%s\" property=\"%s\"/>", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName), fieldName)).append('\n');
        }
        builder.append("</resultMap>");
        System.out.println(builder);
    }

}
