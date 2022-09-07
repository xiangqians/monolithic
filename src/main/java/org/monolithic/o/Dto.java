package org.monolithic.o;

/**
 * DTO（Data Transfer Object），数据传输对象，用于Service层
 *
 * @author xiangqian
 * @date 17:43 2022/06/11
 */
public interface Dto extends O {

    /**
     * {@link Dto} 转为 {@link Vo}
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends Vo> T convertToVo(Class<T> type) {
        throw new UnsupportedOperationException();
    }

}
