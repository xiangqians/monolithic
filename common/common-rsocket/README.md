# 简介

https://rsocket.io

RSocket 是一种高效的异步通信协议和框架，用于构建分布式、实时、可扩展的应用程序。它的设计目标是解决传统 RPC（Remote Procedure Call）框架在处理异步、流式数据和多路复用时的一些限制和性能瓶颈。

RSocket四种数据交互模式：

- Request-And-Response：请求/响应，类似于HTTP的通信特点，提供异步通信与多路复用支持。**（1 : 1）**
- Request-Response-Stream：请求/流式响应，一个请求对应多个流式的响应，例如：获取视频列表或产品列表。**（1 : N）**
- Fire-And-Forget：异步触发，不需要响应，可以用于进行日志记录。**（1 : 0）**
- Channel(bi-directional streams)：双向异步通讯，消息流在服务端与客户端两个方向上异步流动。**（N : N）**


