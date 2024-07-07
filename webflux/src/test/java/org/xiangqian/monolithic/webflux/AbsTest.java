package org.xiangqian.monolithic.webflux;

import java.util.function.Consumer;

/**
 * @author xiangqian
 * @date 20:43 2023/10/13
 */
public abstract class AbsTest {

    protected Consumer<Object> onNext = data -> System.out.format("Next: %s", data).println();

    protected Consumer<Object> on = data -> System.out.format("%s", data).println();

    protected Runnable onComplete = () -> System.out.println("Complete");

    protected Consumer<Throwable> onError = t -> System.err.format("Error: %s", t).println();

}
