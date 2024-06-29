package org.xiangqian.monolithic.consumer;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiangqian
 * @date 19:34 2024/06/26
 */
public class ConsumerTest {

    private void pint(ConsumerRecord<String, String> record) {
        System.out.printf("%s\n\n", DefaultKafka.toString(record));
    }

    private void poll(DefaultKafka kafka) {
        System.out.printf("【%s, %s】poll\n", Thread.currentThread().getName(), Thread.currentThread().getId());

        // 循环拉取消息
        while (true) {
            ConsumerRecords<String, String> records = kafka.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                pint(record);
            }
        }
    }

    // 拉取消息（自动提交偏移量）
    @Test
    @SneakyThrows
    public void testPoll() {
        DefaultKafka kafka = null;
        try {
            String groupId = "my-group-id";
            kafka = DefaultKafka.create("localhost:9092", groupId, Offset.LATEST, true);

            String topic = "my-topic";
            kafka.subscribe(topic);

            poll(kafka);
        } finally {
            kafka.destroy();
        }
    }

    // 多线程拉取消息（自动提交偏移量）
    @Test
    @SneakyThrows
    public void testMultiThreadPoll() {
        String[] groupIds = new String[]{
                "my-group-id1",
                "my-group-id1",
                "my-group-id2"
        };
        CountDownLatch countDownLatch = new CountDownLatch(groupIds.length);
        for (String groupId : groupIds) {
            new Thread(() -> {
                DefaultKafka kafka = null;
                try {
                    kafka = DefaultKafka.create("localhost:9092", groupId, Offset.LATEST, true);

                    String topic = "my-topic";
                    kafka.subscribe(topic);

                    poll(kafka);
                } finally {
                    kafka.destroy();
                    countDownLatch.countDown();
                }
            }, groupId).start();
        }
        countDownLatch.await();
    }

    // 拉取消息（手动提交偏移量）
    @Test
    @SneakyThrows
    public void testPollManualCommit() {
        DefaultKafka kafka = null;
        try {
            String groupId = "my-group-id";
            kafka = DefaultKafka.create("localhost:9092", groupId, Offset.EARLIEST, false);

            String topic = "my-topic";
            kafka.subscribe(topic);

            // 循环拉取消息
            while (true) {
                ConsumerRecords<String, String> records = kafka.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    pint(record);

                    // 手动同步提交当前消费者组的指定分区的偏移量
                    kafka.commitSync(record);
                }
            }
        } finally {
            kafka.destroy();
        }
    }

}
