package org.monolithic.o;

/**
 * VO Param
 *
 * @author xiangqian
 * @date 18:04 2022/06/11
 */
public interface Vpo extends O {

    default <T extends Bpo> T convertToBoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    default <T extends Ppo> T convertToPoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    default <T extends Dtpo> T convertToDtoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * 处理参数
     */
    default void post() {
    }

}
