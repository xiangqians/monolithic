package org.xiangqian.monolithic.monitor.configuration;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.*;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.notify.Notifier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xiangqian.monolithic.util.DateTimeUtil;
import org.xiangqian.monolithic.util.Http;
import org.xiangqian.monolithic.util.Mail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JVM通知器
 * <p>
 * {@link de.codecentric.boot.admin.server.config.AdminServerNotifierAutoConfiguration}
 *
 * @author xiangqian
 * @date 19:30 2024/06/04
 */
@Slf4j
@Component
public class MailNotifier extends de.codecentric.boot.admin.server.notify.MailNotifier implements Notifier, Runnable, ApplicationRunner {

    @Autowired
    private InstanceRepository repository;

    @Autowired
    private Mail mail;

    private List<String> tos;

    private Http http;

    private DecimalFormat decimalFormat;

    public MailNotifier(@Value("${spring.boot.admin.secret}") String secret, @Value("${spring.boot.admin.notify.mail}") String tos) {
        super(null, null, null);

        this.tos = Arrays.stream(tos.split(",")).collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Secret", secret);
        this.http = new Http(new RestTemplate(), headers);

        // 保留两位小数，不四舍五入
        this.decimalFormat = new DecimalFormat("0.00");
        this.decimalFormat.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public Mono<Void> notify(InstanceEvent event) {
        // 实例注册事件
        if (event instanceof InstanceRegisteredEvent) {
            return Mono.empty();
        }

        // 实例状态变更事件
        if (event instanceof InstanceStatusChangedEvent) {
            return Mono.fromRunnable(() -> run((InstanceStatusChangedEvent) event));
        }

        if (event instanceof InstanceEndpointsDetectedEvent) {
            return Mono.empty();
        }

        if (event instanceof InstanceInfoChangedEvent) {
            return Mono.empty();
        }

        // 实例重新注册事件
        if (event instanceof InstanceRegistrationUpdatedEvent) {
            return Mono.empty();
        }

//        log.debug("待处理事件：{}", event);
        return Mono.empty();
    }

    private void run(InstanceStatusChangedEvent event) {
        InstanceId instanceId = event.getInstance();
        Mono<Instance> instanceMono = repository.find(instanceId);
        instanceMono.subscribe(instance -> {
            try {
                run(instance, event);
            } catch (Exception e) {
                log.error("处理【实例状态变更事件（InstanceStatusChangedEvent）】异常", e);
            }
        });
    }

    private void run(Instance instance, InstanceStatusChangedEvent event) {
        // 状态信息
        StatusInfo statusInfo = event.getStatusInfo();

        // 邮件主题
        String subject = null;

        // 服务健康检查未通过
        if (statusInfo.isDown()) {
            subject = "服务健康检查未通过";
        }
        // 服务上线
        else if (statusInfo.isUp()) {
            subject = "服务上线";
        }
        // 服务离线
        else if (statusInfo.isOffline()) {
            subject = "服务离线";
        }
        // 服务未知异常
        else if (statusInfo.isUnknown()) {
            subject = "服务未知异常";
        }

        // 邮件正文
        StringBuilder textBuilder = new StringBuilder();

        Registration registration = instance.getRegistration();
        textBuilder.append("服务名：").append(registration.getName());
        textBuilder.append("\n").append("服务地址：").append(registration.getServiceUrl());

        // 状态信息详情
        Map<String, Object> details = statusInfo.getDetails();

        Object exception = details.get("exception");
        if (exception != null) {
            textBuilder.append("\n").append("异常：").append(exception);
        }

        Object message = details.get("message");
        if (message != null) {
            textBuilder.append("\n").append("信息：").append(message);
        }
        textBuilder.append("\n").append("时间：").append(DateTimeUtil.format(DateTimeUtil.ofInstant(event.getTimestamp())));

        send(subject, textBuilder.toString());
    }

    @SneakyThrows
    private void run(Instance instance) {
        double maxHeapMemory = getMaxHeapMemory(instance);
        String maxHeapMemoryStr = decimalFormat.format(maxHeapMemory) + "MB";
        log.debug("最大堆内存：{}", maxHeapMemoryStr);

        double usedHeapMemory = getUsedHeapMemory(instance);
        String usedHeapMemoryStr = decimalFormat.format(usedHeapMemory) + "MB";
        log.debug("已使用堆内存：{}", usedHeapMemoryStr);

        int liveThreadCount = getLiveThreadCount(instance);
        log.debug("活动线程数量：{}", liveThreadCount);

        int peakThreadCount = getPeakThreadCount(instance);
        log.debug("峰值线程数量：{}", peakThreadCount);
    }

    /**
     * 获取峰值线程数量
     *
     * @param instance
     * @return
     */
    public int getPeakThreadCount(Instance instance) {
        Object metricsValue = getMetricsValue(instance, "/jvm.threads.peak");
        int peakThreadCount = ((Number) metricsValue).intValue();
        return peakThreadCount;
    }

    /**
     * 获取活动线程数量
     *
     * @param instance
     * @return
     */
    public int getLiveThreadCount(Instance instance) {
        Object metricsValue = getMetricsValue(instance, "/jvm.threads.live");
        int liveThreadCount = ((Number) metricsValue).intValue();
        return liveThreadCount;
    }

    /**
     * 获取已使用堆内存（MB）
     *
     * @param instance
     * @return
     */
    public double getUsedHeapMemory(Instance instance) {
        // 单位：Byte
        Object metricsValue = getMetricsValue(instance, "/jvm.memory.used?tag=area:heap");
        double usedHeapMemory = ((Number) metricsValue).doubleValue();

        // 单位：MB
        usedHeapMemory = usedHeapMemory / (1024d * 1024d);
        return usedHeapMemory;
    }

    /**
     * 获取最大堆内存（MB）
     *
     * @param instance
     * @return
     */
    public double getMaxHeapMemory(Instance instance) {
        // 单位：Byte
        Object metricsValue = getMetricsValue(instance, "/jvm.memory.max?tag=area:heap");
        double maxHeapMemory = ((Number) metricsValue).doubleValue();

        // 单位：MB
        maxHeapMemory = maxHeapMemory / (1024d * 1024d);
        return maxHeapMemory;
    }

    private Object getMetricsValue(Instance instance, String url) {
        url = instance.getRegistration().getManagementUrl() + "/metrics" + url;
        Map map = http.get(url, null, Map.class);
        List list = (List) map.get("measurements");
        Map measurement = (Map) list.get(0);
        return measurement.get("value");
    }

    private void send(String subject, String text) {
        log.debug("发送邮件给 {}\n邮件主题：{}\n邮件正文：\n{}", StringUtils.join(tos, ", "), subject, text);
        if (true) {
            return;
        }
        for (String to : tos) {
            mail.send(to, subject, text);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Flux<Instance> flux = repository.findAll();
                flux.subscribe(this::run);
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                log.error("", e);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(this).start();
    }

}
