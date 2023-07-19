package org.xiangqian.monolithic.model;

import org.springframework.beans.BeanUtils;

/**
 * object
 *
 * @author xiangqian
 * @date 20:16 2023/05/05
 */
public interface O {

    default <T> T copyProperties(Class<T> type) throws Exception {
        T target = type.getConstructor().newInstance();
        BeanUtils.copyProperties(this, target);
        return target;
    }

}
