package org.xiangqian.monolithic.consumer.pay.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 支付交易服务
 *
 * @author xiangqian
 * @date 19:23 2024/06/26
 */
public interface TransactionService {

    String CREATE = "pay-transaction-create";
    String CALLBACK = "pay-transaction-callback";
    String REFUND = "pay-transaction-refund";

    /**
     * 创建支付交易（Create Payment Transaction）
     * 发起新的支付交易。
     *
     * @param record
     */
    void create(ConsumerRecord<String, String> record);


    /**
     * 处理支付回调（Handle Payment Callback）
     * 用于接收第三方支付平台的支付结果通知，更新交易状态和执行后续操作。
     *
     * @param record
     */
    void callback(ConsumerRecord<String, String> record);

    /**
     * 退款（Refund Transaction）
     * 支持对已完成支付的交易进行退款操作。
     *
     * @param record
     */
    void refund(ConsumerRecord<String, String> record);

}
