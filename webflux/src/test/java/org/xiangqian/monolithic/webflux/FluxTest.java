package org.xiangqian.monolithic.webflux;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Flux 表示的是包含 0 到 N 个元素的异步序列。
 * 序列中包含了3种类型的消息通知：正常的包含元素的消息、序列结束的消息、序列出错的消息。当消息通知产生时，订阅者中对应的方法会被调用onNext()、onComplete()、onError()会被调用。
 *
 * @author xiangqian
 * @date 20:58 2023/10/13
 */
public class FluxTest extends AbsTest {

    // 创建Flux之just
    @Test
    public void just() {
        // 可以指定序列中包含的全部元素，创建出来的 Flux 序列在发布这些元素之后会自动结束
        Flux.just("Hello", "World")
                // 正常的包含元素的消息
                .doOnNext(onNext)
                // 序列结束的消息
                .doOnComplete(onComplete)
                // 序列出错的消息
                .doOnError(onError)
                // 当需要处理 Flux 或 Mono 中的消息时，可以通过 subscribe 方法来添加相应的订阅逻辑。
                // 在调用 subscribe 方法时可以指定需要处理的消息类型。可以只处理其中包含的正常消息，也可以同时处理错误消息和完成消息。
                .subscribe(on);
    }

    @Test
    public void justError1() {
        Flux.just("Hello", "World")
                .concatWith(Mono.error(new IllegalStateException()))
                .subscribe(on, onError, onComplete);
    }

    @Test
    public void justError2() {
        Flux.just("Hello", "World")
                .concatWith(Mono.error(new IllegalStateException()))
                // 出现错误时返回默认值
                .onErrorReturn("Default")
                .subscribe(on, onError, onComplete);
    }

    // 创建Flux之range
    @Test
    public void range() {
        // 创建包含从 start 起始的 count 个数量的 Integer 对象的序列
        int start = 1;
        int count = 10;
        Flux.range(start, count)
                .doOnNext(onNext)
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe(on);
    }

    // 操作符：buffer和bufferTimeout
    @Test
    public void buffer() {
        Flux.range(1, 100)
                .doOnNext(onNext)
                .doOnComplete(onComplete)
                .doOnError(onError)
                // 作用是把当前流中的元素收集到集合中，并把集合对象作为流中的新元素。
                .buffer(20)
                .subscribe(on);
    }

    // 操作符：filter
    @Test
    public void filter() {
        Flux.range(1, 10)
                .doOnNext(onNext)
                .doOnComplete(onComplete)
                .doOnError(onError)
                // 作用对流中包含的元素进行过滤，只留下满足 Predicate 指定条件的元素。
                .filter(i -> i % 2 == 0)
                .subscribe(on);
    }

    // 操作符：take
    @Test
    public void take() {
        Flux.range(1, 1000)
                // 作用用来从当前流中提取元素
                .take(5) // 取出5个
                .subscribe(on);
    }

    // 操作符：reduce和reduceWith
    @Test
    public void reduce() {
        Flux.range(1, 100)
                // 作用对流中包含的所有元素进行累积操作，得到一个包含计算结果的 Mono 序列
                .reduce((x, y) -> x + y)
                .subscribe(on);

    }

    // 操作符：flatMap和flatMapSequential
    @Test
    public void flatMap() {
        Flux.just(2, 4)
                // 作用把流中的每个元素转换成一个流，再把所有流中的元素进行合并
                // 流中的元素被转换成每隔 1000ms 产生的数量不同的流，再进行合并。由于第一个流中包含的元素数量较少，所以在结果流中一开始是两个流的元素交织在一起，然后就只有第二个流中的元素。
                .flatMap(x -> Flux.interval(Duration.ofMillis(1000)).take(x))
                .toStream()
                .forEach(on);
    }

    // 调度器
    // 单一的可复用的线程，通过 Schedulers.single() 方法来创建。
    // 使用弹性的线程池，通过 Schedulers.elastic()方 法来创建，线程池中的线程是可以复用的，当所需要时，新的线程会被创建，如果一个线程闲置太长时间，则会被销毁，该调度器适用于 I/O 操作相关的流的处理。
    // 使用对并行操作优化的线程池，通过 Schedulers.parallel() 方法来创建，其中的线程数量取决于 CPU 的核的数量，该调度器适用于计算密集型的流的处理。
    // 当前线程，通过 Schedulers.immediate() 方法来创建
    @Test
    public void scheduler() throws Exception {
        // 不指定线程池
        Flux.range(1, 5)
                .doOnRequest(n -> System.out.format("Request %s", n).println()) // 注意顺序造成的区别
                .publishOn(Schedulers.parallel()) // 指定publish线程池
                .subscribeOn(Schedulers.parallel()) // 指定subscribe线程池
                .doOnComplete(() -> System.out.println("Publisher Complete 1"))
                .map(i -> {
//                    i = 10 / (i - 3);
                    Thread curThread = Thread.currentThread();
                    System.out.format("[%s, %s] Publish %s", curThread.getId(), curThread.getName(), i).println();
                    return i;
                })
                .doOnComplete(() -> System.out.println("Publisher Complete 2"))
                .subscribe(i -> {
                            Thread curThread = Thread.currentThread();
                            System.out.format("[%s, %s] Subscribe %s", curThread.getId(), curThread.getName(), i).println();
                        },
                        e -> System.err.format("Error %s", e.getMessage()).println(),
                        () -> System.out.println("Subscriber Complete"));
        TimeUnit.SECONDS.sleep(2);
    }

}
