package org.xiangqian.monolithic.webflux;

import lombok.SneakyThrows;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Mono 表示的是包含 0 或者 1 个元素的异步序列。
 * Flux 和 Mono 之间可以进行转换，对一个 Flux 序列进行计数操作，得到的结果是一个 Mono对象。把两个 Mono 序列合并在一起，得到的是一个 Flux 对象。
 *
 * @author xiangqian
 * @date 20:43 2023/10/13
 */
public class MonoTest extends AbsTest {

    // 创建Mono之create
    @Test
    public void create() {
        // 使用 MonoSink 来创建 Mono
        Consumer<MonoSink<String>> callback = monoSink -> monoSink.success("Hello World!");
        Mono.create(callback)
                .doOnNext(onNext)
                .doOnError(onError)
                .subscribe(on);
    }

    // 创建Mono之just
    @Test
    public void just() {
        Mono.just("Hello World!")
                .doOnNext(onNext)
                .doOnError(onError)
                .subscribe(on);
    }

    // 创建Mono之justOrEmpty
    @Test
    public void justOrEmpty() {
        Mono.justOrEmpty(null)
                .doOnNext(onNext)
                .doOnError(onError)
                .subscribe(on);
    }

    // 创建Mono之error
    @Test
    public void error() {
        // 创建一个只包含错误消息的 Mono
        Mono.error(new NullPointerException())
                .doOnNext(onNext)
                .doOnError(onError)
                .subscribe(on);
    }

    // 创建Mono之never
    @Test
    public void never() {
        // 创建一个不包含任何消息通知的 Mono
        Mono.never()
                .doOnNext(onNext)
                .doOnError(onError)
                .subscribe(on);
    }

    // 创建Mono之delay
    @Test
    @SneakyThrows
    public void delay() {
        // 在指定的延迟时间之后，创建一个 Mono，产生数字 0 作为唯一值
        Mono<Long> mono = Mono.delay(Duration.ofSeconds(2))
                .doOnNext(onNext)
                .doOnError(onError);

//        mono.subscribe(on);
        on.accept(mono.block());
    }

}
