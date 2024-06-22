package org.xiangqian.monolithic.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置（使用纯粹的 WebSocket API）
 * <p>
 * <p>
 * WebSocket 是一种基于 TCP 协议的全双工通信协议，它允许客户端和服务器之间建立持久的、双向的通信连接。
 * 相比传统的 HTTP 请求 - 响应模式，WebSocket 提供了实时、低延迟的数据传输能力。通过 WebSocket，客户端和服务器可以在任意时间点互相发送消息，实现实时更新和即时通信的功能。
 * WebSocket 协议经过了多个浏览器和服务器的支持，成为了现代 Web 应用中常用的通信协议之一。它广泛应用于聊天应用、实时数据更新、多人游戏等场景，为 Web 应用提供了更好的用户体验和更高效的数据传输方式。
 * <p>
 * <p>
 * Spring Boot 集成 WebSocket 两种主要方式：
 * 方式一：使用纯粹的 WebSocket API
 * 由 Jakarta EE 规范提供的 Api，也就是 jakarta.websocket 包下的接口。
 * 这种方式是通过 @EnableWebSocket 注解来启用纯粹的 WebSocket 支持。
 * 优点：更接近原生 WebSocket API，对于简单的应用或者对 WebSocket 原生特性有特定需求的情况下，更为直观和简单。
 * 缺点：需要手动处理很多 WebSocket 的细节，如连接管理、消息路由等，相对于 STOMP 协议的集成要更为复杂。
 * <p>
 * 方式二：使用 STOMP 协议和简单消息代理
 * 由 spring 提供的支持，也就是 spring-websocket 模块。前者（方式一）是一种独立于框架的技术规范，而后者（方式二）是 Spring 生态系统的一部分，可以与其他 Spring 模块（如 Spring MVC、Spring Security）无缝集成，共享其配置和功能。
 * 这种方式是通过 @EnableWebSocketMessageBroker 注解来启用 WebSocket 消息代理和 STOMP 协议支持。
 * 优点：Spring 提供了高度集成的 STOMP 支持，包括消息代理、消息转发、消息订阅和广播等功能。可以轻松实现复杂的消息交互模式。
 * 缺点：相对于纯粹的 WebSocket，会增加一些额外的复杂性和开销。
 *
 * @author xiangqian
 * @date 19:18 2024/06/17
 */
@Configuration
public class WebsocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();

        // 手动注册 WebSocket 端点
//        serverEndpointExporter.setAnnotatedEndpointClasses();
        // 也可以在 WebSocket 端点上添加 @Component 注解，使用 Spring 自动扫描，这样的话不需要手动调用 setAnnotatedEndpointClasses 方法进行注册

        return serverEndpointExporter;
    }

}
