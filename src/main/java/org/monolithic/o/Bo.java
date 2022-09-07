package org.monolithic.o;

/**
 * BO（Business Object），业务对象，可以把BO看作是PO的组合，用于DAO层
 *
 * @author xiangqian
 * @date 14:53 2022/06/11
 */
public interface Bo extends O {

    /**
     * {@link Bo} 转为 {@link Vo}
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends Vo> T convertToVo(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link Bo} 转为 {@link Dto}
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends Dto> T convertToDto(Class<T> type) {
        throw new UnsupportedOperationException();
    }

}
