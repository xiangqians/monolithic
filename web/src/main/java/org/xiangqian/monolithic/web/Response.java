package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.Description;
import org.xiangqian.monolithic.biz.ResourceUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xiangqian
 * @date 21:07:15 2022/03/26
 */
@Data
@NoArgsConstructor
@Schema(description = "响应信息")
public class Response<T> {

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    public Response(String code) {
        this(code, null);
    }

    public Response(String code, T data) {
        this(code, codeDescriptionMap.get(code), data);
    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // Map<Code, Description>
    private static final Map<String, String> codeDescriptionMap;

    static {
        try {
            codeDescriptionMap = new HashMap<>(64, 1f);
            Set<Class> classes = ResourceUtil.getClasses("org.xiangqian.monolithic.biz.**");
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
                            codeDescriptionMap.put((String) field.get(null), description.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
