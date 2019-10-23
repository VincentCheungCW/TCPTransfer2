package ch.TCPTransfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static ch.TCPTransfer.Start.GPSAddr;
import static ch.TCPTransfer.Start.GPSPort;

/**
 * Created by Jiang on 2019-10-17.
 */
public class Client {
    private EventLoopGroup loop = new NioEventLoopGroup();

    Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop) {
        if (bootstrap != null) {
            final ClientHandler handler = new ClientHandler(this);
            bootstrap.group(eventLoop);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(handler);
                }
            });
            bootstrap.remoteAddress(GPSAddr, Integer.valueOf(GPSPort));
            bootstrap.connect().addListener(new ConnectionListener(this));
        }
        return bootstrap;
    }

    public void run() {
        createBootstrap(new Bootstrap(), loop);
    }

    public void stop() {
        loop.shutdownGracefully();
    }
}
