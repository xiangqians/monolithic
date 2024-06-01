package org.xiangqian.monolithic.biz;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author xiangqian
 * @date 17:19 2024/06/01
 */
public interface Assert {

    static void isTrue(boolean expression, String code) {
        if (!expression) {
            throw new CodeException(code);
        }
    }

    static void isNull(Object object, String code) {
        if (object != null) {
            throw new CodeException(code);
        }
    }

    static void notNull(Object object, String code) {
        if (object == null) {
            throw new CodeException(code);
        }
    }

    static void notEmpty(String string, String code) {
        if (StringUtils.isEmpty(string)) {
            throw new CodeException(code);
        }
    }

    static void notEmpty(Object[] array, String code) {
        if (ArrayUtils.isEmpty(array)) {
            throw new CodeException(code);
        }
    }

    static void notEmpty(Collection collection, String code) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new CodeException(code);
        }
    }

    static void notEmpty(Map map, String code) {
        if (MapUtils.isEmpty(map)) {
            throw new CodeException(code);
        }
    }

}
