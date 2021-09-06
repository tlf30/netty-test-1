package io.tlf.netty.test.app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Test67Server {
    private static final int PORT = 8067;

    public void run() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();

        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(SslProvider.JDK)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(
                        new ApplicationProtocolConfig(
                                ApplicationProtocolConfig.Protocol.ALPN,
                                ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                ApplicationProtocolNames.HTTP_2,
                                ApplicationProtocolNames.HTTP_1_1
                        )
                )
                .build();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    sslCtx.newHandler(ch.alloc()),
                                    new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

                                        @Override
                                        protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                                            if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                                                ctx.pipeline().addLast(
                                                        Http2FrameCodecBuilder.forServer().build(),
                                                        new Test67ResponseHandler()
                                                );
                                                return;
                                            }
                                            throw new IllegalStateException("Protocol: " + protocol + " not supported");
                                        }
                                    });
                        }

                    });

            Channel ch = b.bind(PORT).sync().channel();

            System.out.println("Test Server on https://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
