package org.xiangqian.monolithic.common.producer;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.kafka.support.SendResult;
import org.xiangqian.monolithic.common.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiangqian
 * @date 21:49 2024/06/25
 */
public class ProducerTest {

    private Producer producer;

    @Before
    public void before() {
        producer = Producer.create();
    }

    @After
    public void after() {
        IOUtils.closeQuietly(producer);
    }

    @Test
    @SneakyThrows
    public void testSend1() {
        int count = 10;
        while (count-- > 0) {
            String topic = "my-topic";
            String key = "my-key";
            String value = "Hello, Kafka! " + DateTimeUtil.format(LocalDateTime.now()) + " " + UUID.randomUUID().toString().replace("-", "");
            CompletableFuture<SendResult<String, String>> future = producer.send(topic, key, value);
            SendResult<String, String> sendResult = future.get();
            System.out.println(sendResult);
        }
    }

    @Test
    @SneakyThrows
    public void testSend2() {
        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        while (count-- > 0) {
            new Thread(() -> {
                testSend1();
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
    }

    @Test
    @SneakyThrows
    public void testSend12() {

    }

}
