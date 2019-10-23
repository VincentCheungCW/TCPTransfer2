package ch.TCPTransfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

/**
 * UDP组播发送
 */
public class UDPBroadCaster {
    private EventLoopGroup group;
    private static InetSocketAddress groupAddressSend;
    private static Channel ch;

    public UDPBroadCaster(InetSocketAddress address, InetAddress local) throws Exception {
        groupAddressSend = address;
        NetworkInterface ni = NetworkInterface.getByInetAddress(local);
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        //引导该 NioDatagramChannel（无连接的）
        bootstrap.group(group)
                .channelFactory(new ChannelFactory<NioDatagramChannel>() {
                    public NioDatagramChannel newChannel() {
                        return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    }
                })
                .localAddress(local, groupAddressSend.getPort())
                .option(ChannelOption.IP_MULTICAST_IF, ni)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) throws Exception {
                        //ch.pipeline().addLast(new ClientMulticastHandler());
                    }
                });
        ch = bootstrap.bind(0).sync().channel();
    }

    public void send(ByteBuf buf) {
        try {
            ch.writeAndFlush(new DatagramPacket(buf, groupAddressSend)).sync();
        } catch (Exception e) {
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }
}
