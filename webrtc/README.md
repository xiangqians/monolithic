# 简介

实时传输协议（Real-Time Transport Protocols，RTP）是一组协议，用于在计算机网络上实现实时数据传输，特别是用于音频和视频数据的传输。

以下是几种常见的实时传输协议：

- RTP（Real-Time Transport Protocol）

RTP是一个标准协议，用于传输音频和视频数据，通常与RTCP（RTP Control Protocol）一起使用。RTP提供时间戳、序列号和分段等功能，以便接收方能够重构实时数据流。

- RTSP（Real-Time Streaming Protocol）

RTSP是一个网络控制协议，用于控制流媒体服务器上的实时流媒体数据的传输。它通常用于客户端与服务器之间的命令和控制消息交换，而实际的媒体数据传输可能使用RTP或者其他协议。

- WebRTC（Web Real-Time Communication）

WebRTC 是一个开放的实时通信协议，提供浏览器之间点对点的音频、视频和数据传输能力。它使用了一系列协议和技术，包括SRTP（Secure Real-Time Transport Protocol）用于加密和保护数据。

- SRT（Secure Reliable Transport）

SRT是一种开源协议，用于优化传输实时流媒体的性能，特别是在不稳定网络条件下。它结合了UDP的速度和TCP的可靠性，并提供了加密和低延迟传输的功能。

- HLS（HTTP Live Streaming）

HLS是由苹果公司开发的基于HTTP的流媒体协议，虽然不是严格意义上的实时传输协议，但被广泛用于分发实时直播和点播内容。它通过将媒体流切分成小块（segments），并使用HTTP协议进行传输，以支持不同网络环境下的流畅播放。

- 对比





