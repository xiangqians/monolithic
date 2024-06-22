package org.xiangqian.monolithic.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.util.UUID;

/**
 * js工具
 *
 * @author xiangqian
 * @date 21:08 2024/01/02
 */
public class JsUtil {

    /**
     * 执行js代码
     *
     * @param source           js代码
     * @param dependentSources js库
     * @return
     * @throws IOException
     */
    public static Object execute(String source, String... dependentSources) {
        try {
            // 创建js上下文
            Context context = Context.enter();

            // 初始化全局作用域
            Scriptable scope = context.initStandardObjects();

            // 加载js库
            if (ArrayUtils.isNotEmpty(dependentSources)) {
                for (String dependentSource : dependentSources) {
                    context.evaluateString(scope, dependentSource, UUID.randomUUID().toString().replace("-", ""), 1, null);
                }
            }

            // 执行js代码
            return context.evaluateString(scope, source, "<cmd>", 1, null);
        } finally {
            // 退出JavaScript上下文
            Context.exit();
        }
    }

}
