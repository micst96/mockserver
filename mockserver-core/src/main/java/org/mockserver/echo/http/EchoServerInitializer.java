package org.mockserver.echo.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.mockserver.logging.LoggingHandler;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.codec.MockServerServerCodec;
import org.mockserver.socket.tls.NettySslContextFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockserver.echo.http.EchoServer.*;
import static org.slf4j.event.Level.TRACE;

/**
 * @author jamesdbloom
 */
public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {

    private final MockServerLogger mockServerLogger;
    private final boolean secure;
    private final EchoServer.Error error;
    private final List<TextWebSocketFrame> textWebSocketFrames;
    private final List<Channel> websocketChannels;
    private final List<String> registeredClients;

    EchoServerInitializer(MockServerLogger mockServerLogger, boolean secure, EchoServer.Error error, List<String> registeredClients, List<Channel> websocketChannels, List<TextWebSocketFrame> textWebSocketFrames) {
        if (!secure && error == EchoServer.Error.CLOSE_CONNECTION) {
            throw new IllegalArgumentException("Error type CLOSE_CONNECTION is not supported in non-secure mode");
        }
        this.mockServerLogger = mockServerLogger;
        this.secure = secure;
        this.error = error;
        this.registeredClients = registeredClients;
        this.websocketChannels = websocketChannels;
        this.textWebSocketFrames = textWebSocketFrames;
    }

    public void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();

        if (error != null) {
            pipeline.addLast(new ErrorHandler(error));
        }

        if (secure) {
            pipeline.addLast(new NettySslContextFactory(mockServerLogger).createServerSslContext().newHandler(channel.alloc()));
        }

        if (MockServerLogger.isEnabled(TRACE)) {
            pipeline.addLast(new LoggingHandler("EchoServer <-->"));
        }

        pipeline.addLast(new HttpServerCodec());

        pipeline.addLast(new HttpContentDecompressor());

        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

        pipeline.addLast(new WebSocketServerHandler(mockServerLogger, registeredClients, websocketChannels, textWebSocketFrames, secure));

        pipeline.addLast(new MockServerServerCodec(mockServerLogger, secure));

        if (!secure && error == EchoServer.Error.CLOSE_CONNECTION) {
            throw new IllegalArgumentException("Error type CLOSE_CONNECTION is not supported in non-secure mode");
        }

        pipeline.addLast(new EchoServerHandler(
            error,
            mockServerLogger,
            channel.attr(LOG_FILTER).get(),
            channel.attr(NEXT_RESPONSE).get()
        ));
    }
}
