package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.Description;
import org.xiangqian.monolithic.biz.auth.AuthCode;
import org.xiangqian.monolithic.biz.sys.SysCode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Class> classes = List.of(Code.class, AuthCode.class, SysCode.class);

        int size = 0;
        List<Field[]> fieldsList = new ArrayList<>(classes.size());
        for (Class clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            size += fields.length;
            fieldsList.add(fields);
        }
        codeDescriptionMap = new HashMap<>(size, 1f);

        for (Field[] fields : fieldsList) {
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                Description description = null;
                if (Modifier.isPublic(modifiers)
                        && Modifier.isStatic(modifiers)
                        && Modifier.isFinal(modifiers)
                        && field.getType() == String.class
                        && (description = field.getAnnotation(Description.class)) != null) {
                    try {
                        codeDescriptionMap.put((String) field.get(null), description.value());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
            }
        }
    }

}
