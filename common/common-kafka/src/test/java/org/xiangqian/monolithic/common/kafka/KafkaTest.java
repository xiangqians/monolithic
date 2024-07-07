package org.xiangqian.monolithic.common.kafka;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.kafka.support.SendResult;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiangqian
 * @date 21:49 2024/06/25
 */
public class KafkaTest {

    private Kafka kafka;

    @Before
    public void before() {
        kafka = Kafka.create();
    }

    @After
    @SneakyThrows
    public void after() {
        kafka.destroy();
    }

    @Test
    @SneakyThrows
    public void testSend1() {
        int count = 10;
        while (count-- > 0) {
            String topic = "my-topic";
            String key = "my-key";
            String value = "Hello, Kafka! " + DateTimeUtil.format(LocalDateTime.now()) + " " + UUID.randomUUID().toString().replace("-", "");
            CompletableFuture<SendResult<String, String>> future = kafka.send(topic, key, value);
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
