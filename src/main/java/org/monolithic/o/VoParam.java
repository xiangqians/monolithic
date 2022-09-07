package org.monolithic.o;

/**
 * VO Param
 *
 * @author xiangqian
 * @date 18:04 2022/06/11
 */
public interface VoParam extends O {

    default <T extends BoParam> T convertToBoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    default <T extends PoParam> T convertToPoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    default <T extends DtoParam> T convertToDtoParam(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * 处理参数
     */
    default void post() {
    }

}
