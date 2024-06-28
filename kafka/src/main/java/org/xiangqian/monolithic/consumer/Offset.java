package org.xiangqian.monolithic.consumer;

import lombok.Getter;

/**
 * Kafka 主题中的每个分区都有一个偏移量，用于跟踪消费者已经处理到的消息位置。
 * 当消费者启动或者发生了一些异常情况（例如消费者组没有偏移量或偏移量无效），就需要根据 {@link org.apache.kafka.clients.consumer.ConsumerConfig#AUTO_OFFSET_RESET_CONFIG} 来决定从何处开始消费消息。
 *
 * @author xiangqian
 * @date 19:07 2024/06/26
 */
public enum Offset {
    /**
     * 如果消费者组之前没有消费过该分区或偏移量无效，则从该分区的最早消息开始消费。换句话说，从最早的可用消息开始消费，即从分区的起始位置开始。
     */
    EARLIEST("earliest"),

    /**
     * 如果消费者组之前没有消费过该分区或偏移量无效，则从该分区的最新消息开始消费。也就是说，只消费从当前时间点之后生产的消息。（默认值）
     */
    LATEST("latest"),

    /**
     * 如果消费者组之前没有消费过该分区或偏移量无效，则抛出异常。一般情况下不建议使用此选项，因为它会导致消费者在启动时发生错误。
     */
    NONE("none"),
    ;

    @Getter
    private final String value;

    Offset(String value) {
        this.value = value;
    }

}
