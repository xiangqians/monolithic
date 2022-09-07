package org.monolithic.o;

/**
 * PO（Persistent Object），持久化对象，和数据库字段一一对应的映射关系，用于DAO层
 * PO等同于Entity
 *
 * @author xiangqian
 * @date 14:54 2022/06/11
 */
public interface Po extends O {

    /**
     * {@link Po} 转为 {@link Vo}
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends Vo> T convertToVo(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link Po} 转为 {@link Dto}
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends Dto> T convertToDto(Class<T> type) {
        throw new UnsupportedOperationException();
    }

}
