package org.xiangqian.monolithic.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.common.util.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * “服务器会为每个连接创建一个端点实例对象”：
 * 运行时的 WebSocket 连接对象，也就是端点实例，是由服务器创建，而不是 Spring，所以不能使用自动装配（@Autowired）。
 *
 * @author xiangqian
 * @date 21:40 2024/06/17
 */
@Slf4j
@Component
@ServerEndpoint(value = "/") // 使用 @ServerEndpoint 注解表示此类是一个 WebSocket 端点。通过 value 注解，指定 websocket 的路径。
public class Endpoint implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Map<{@link Receiver#value()}, {@link java.lang.reflect.Method}>
     */
    private static Map<String, Method> methodMap;

    private EndpointManager endpointManager;

    /**
     * 会话id（Session Id）
     * 服务器会为每个连接分配一个不同的 id 值，不同服务器生成的 id 类型不一样。
     * Tomcat 使用从 0 开始的自增值；Undertow 使用的是类似于 UUID 的 32 位长度的字符串。
     */
    private String id;

    private Session session;

    private String token;

//    private UserEntity user;
//    Object getUser();

    /**
     * 已订阅主题集合
     */
    @Getter
    private Set<String> subscribedTopics;

    public Endpoint() {
        this.endpointManager = getBean(EndpointManager.class);
    }

    /**
     * 订阅主题
     *
     * @param topics
     * @return 订阅主题数
     */
    private synchronized int subscribeTopic(Set<String> topics) {
        if (subscribedTopics == null) {
            subscribedTopics = new HashSet<>(8, 1f);
        }

        int count = 0;
        for (String topic : topics) {
            if (methodMap.containsKey(topic) && subscribedTopics.add(topic)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 取消订阅主题
     *
     * @param topics
     * @return 取消订阅主题数
     */
    private synchronized int unsubscribeTopic(Set<String> topics) {
        if (CollectionUtils.isEmpty(subscribedTopics)) {
            return 0;
        }

        int count = 0;
        for (String topic : topics) {
            if (subscribedTopics.remove(topic)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 连接打开
     * <p>
     * 监听客户端的连接事件，它没有任何属性。
     * 可以作为方法参数的对象有很多，Session 对象是必须的，表示当前连接对象，我们可以通过此对象来执行发送消息、断开连接等操作。
     * WebSocket 的连接 URL，类似于 Http 的 URL，也可以传递查询参数、path 参数。
     * 通常用于传递认证、鉴权用的 Token 或其他信息。
     * <p>
     * onOpen方法在整个连接的生命周期中，只会执行一次，所以这种方式不会带来通信时的性能损耗。
     *
     * @param session
     * @param endpointConfig
     */
    @OnOpen
    public void onOpen(EndpointConfig endpointConfig, Session session) throws IOException {
        this.id = session.getId();
        this.session = session;

        // 获取请求参数
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();

        // 认证
        String token = Optional.ofNullable(requestParameterMap.get("token")).filter(CollectionUtils::isNotEmpty).map(list -> StringUtils.trim(list.get(0))).orElse(null);
        if (StringUtils.isEmpty(token) || !"1".equals(token)) {
            close(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid token");
            return;
        }

        this.token = token;

        endpointManager.add(this);

        log.debug("【新连接】id = {}", id);
    }

    /**
     * 收到消息
     * <p>
     * 监听客户端消息事件，它只有一个属性 long maxMessageSize() default -1; 用于限制客户端消息的大小，如果小于等于 0 则表示不限制。
     * 当客户端消息体积超过这个阈值，那么服务器就会主动断开连接，状态码为：1009。
     * <p>
     * 方法的参数可以是基本的 String / byte[] 或者是 Reader / InputStream，分别表示 WebSocket 中的文本和二进制消息。
     * 也可以是自定义的 Java 对象，但是需要在 @ServerEndpoint 中配置对象的解码器（jakarta.websocket.Decoder）。
     * <p>
     * 对于内容较长的消息，支持分批发送，可以在消息参数后面定义一个布尔类型的 boolean last 参数，如果该值为 true 则表示此消息是批次消息中的最后一条。
     *
     * @param message
     * @param last    此消息是否是批次消息中的最后一条
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message, boolean last) {
        log.debug("【收到消息】" +
                        "\n\t会话id：{}" +
                        "\n\t消息：{}" +
                        "\n\t此消息是否是批次消息中的最后一条：{}",
                id,
                message,
                last);
        onMessage(message);
    }

    private void onMessage(String message) {
        String topic = null;
        try {
            Map map = JsonUtil.deserialize(message, Map.class);
            MapWrapper mapWrapper = new MapWrapper(map);

            topic = mapWrapper.getString("topic");
            if (StringUtils.isEmpty(topic)) {
                send(new Message(topic, Code.NOT_FOUND));
                return;
            }

            // 订阅主题
            if ("/subscribe/topic".equals(topic)) {
                int count = 0;
                List<?> list = mapWrapper.getList("data");
                if (CollectionUtils.isNotEmpty(list)) {
                    count = subscribeTopic(list.stream().map(Object::toString).collect(Collectors.toSet()));
                }
                send(new Message(topic, Code.OK, count));
                return;
            }

            // 取消订阅主题
            if ("/unsubscribe/topic".equals(topic)) {
                int count = 0;
                List<?> list = mapWrapper.getList("data");
                if (CollectionUtils.isNotEmpty(list)) {
                    count = unsubscribeTopic(list.stream().map(Object::toString).collect(Collectors.toSet()));
                }
                send(new Message(topic, Code.OK, count));
                return;
            }

            Method method = methodMap.get(topic);
            if (method == null) {
                send(new Message(topic, Code.NOT_FOUND));
                return;
            }

            Object[] args = null;
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (ArrayUtils.isNotEmpty(parameterTypes)) {
                int length = parameterTypes.length;
                args = new Object[length];
                for (int i = 0; i < length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if (parameterType == Endpoint.class) {
                        args[i] = this;
                    } else if (parameterType == List.class) {
                        args[i] = mapWrapper.getList("data");
                    } else if (parameterType == Map.class) {
                        args[i] = mapWrapper.getMap("data");
                    }
                }
            }

            Object bean = getBean(method.getDeclaringClass());
            Object result = method.invoke(bean, args);
            send(new Message(topic, Code.OK, result));
        } catch (Exception e) {
            log.error("", e);
            if (e instanceof CodeException) {
                send(new Message(topic, e.getMessage()));
            } else {
                send(new Message(topic, Code.ERROR, e.getMessage(), null));
            }
        }
    }


    /**
     * 连接关闭
     *
     * @param closeReason
     */
    @OnClose
    public void onClose(CloseReason closeReason) {
        endpointManager.remove(this);
        CloseReason.CloseCode closeCode = closeReason.getCloseCode();
        log.debug("【连接关闭】" +
                        "\n\t会话id：{}" +
                        "\n\t状态码：{}（{}）" +
                        "\n\t原因：{}",
                id,
                closeCode, closeCode.getCode(),
                closeReason.getReasonPhrase());
    }

    /**
     * 连接异常
     *
     * @param throwable
     * @throws IOException
     */
    @OnError
    public void onError(Throwable throwable) throws IOException {
        endpointManager.remove(this);
        log.error("【连接异常】会话id = " + id, throwable);
        close(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage());
    }

    public void send(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public void send(ByteBuffer message) {
        session.getAsyncRemote().sendBinary(message);
    }

    public void send(byte[] message) {
        send(ByteBuffer.wrap(message));
    }

    @SneakyThrows
    public void send(Object message) {
        send(JsonUtil.serializeAsString(message));
    }

    /**
     * 关闭连接
     *
     * @param code   关闭状态码 {@link jakarta.websocket.CloseReason.CloseCodes}
     *               {@link jakarta.websocket.CloseReason.CloseCodes#NORMAL_CLOSURE}：正常关闭，由服务器主动关闭连接
     *               {@link jakarta.websocket.CloseReason.CloseCodes#CANNOT_ACCEPT}：无法接受
     *               {@link jakarta.websocket.CloseReason.CloseCodes#UNEXPECTED_CONDITION}：意料之外的异常
     * @param reason 关闭原因
     * @throws IOException
     */
    public void close(CloseReason.CloseCode code, String reason) throws IOException {
        session.close(new CloseReason(code, reason));
    }

    /**
     * 正常关闭连接
     *
     * @throws IOException
     */
    public void close() throws IOException {
        close(CloseReason.CloseCodes.NORMAL_CLOSURE, "");
    }

    private Object getBean(String name) {
        if (applicationContext != null) {
            return applicationContext.getBean(name);
        }
        return null;
    }

    private <T> T getBean(Class<T> requiredType) {
        if (applicationContext != null) {
            return applicationContext.getBean(requiredType);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Endpoint.applicationContext = applicationContext;

        methodMap = new HashMap<>(16, 1f);
        Set<Class<?>> classes = ResourceUtil.getClasses("org.xiangqian.monolithic.websocket.*.receiver");
        for (Class<?> clazz : classes) {
            if (Modifier.isInterface(clazz.getModifiers())) {
                Receiver classReceiver = clazz.getAnnotation(Receiver.class);
                if (classReceiver != null) {
                    Method[] methods = clazz.getMethods();
                    if (ArrayUtils.isNotEmpty(methods)) {
                        for (Method method : methods) {
                            Receiver methodReceiver = method.getAnnotation(Receiver.class);
                            if (methodReceiver != null) {
                                String path = classReceiver.value() + methodReceiver.value();
                                Method oldMethod = methodMap.put(path, method);
                                if (oldMethod != null) {
                                    throw new Exception(String.format("Receiver重复：%s,%s", oldMethod, method));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Endpoint that = (Endpoint) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
