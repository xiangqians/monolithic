package org.xiangqian.monolithic.biz;

import org.xiangqian.monolithic.util.ResourceUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xiangqian
 * @date 13:15 2024/06/08
 */
public class CodeDescription {

    // Map<Code, Description>
    private static final Map<String, String> map;

    static {
        try {
            map = new HashMap<>(64, 1f);
            Set<Class<?>> classes = ResourceUtil.getClasses("org.xiangqian.monolithic.biz.**");
            for (Class clazz : classes) {
                if (clazz == Code.class || Code.class.isAssignableFrom(clazz)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        int modifiers = field.getModifiers();
                        Description description = null;
                        if (Modifier.isPublic(modifiers)
                                && Modifier.isStatic(modifiers)
                                && Modifier.isFinal(modifiers)
                                && field.getType() == String.class
                                && (description = field.getAnnotation(Description.class)) != null) {
                            map.put((String) field.get(null), description.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * 获取code描述
     *
     * @param code {@link Code}
     * @return {@link Description}
     */
    public static String get(String code) {
        return map.get(code);
    }

}
