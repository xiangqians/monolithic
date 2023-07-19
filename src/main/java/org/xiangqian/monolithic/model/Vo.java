package org.xiangqian.monolithic.model;

/**
 * Vo（View Object），视图对象
 *
 * @author xiangqian
 * @date 19:22 2023/05/05
 */
public interface Vo extends O {

    /**
     * 验证
     *
     * @param group 验证组
     * @throws Exception
     */
    default void validate(Class<?> group) throws Exception {
    }

}
