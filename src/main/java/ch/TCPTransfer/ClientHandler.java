package ch.TCPTransfer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.TimeUnit;

import static ch.TCPTransfer.Start.groupCaster;

public class ClientHandler extends SimpleChannelInboundHandler {
    private Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    //receive message
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        System.out.println("收到差分码" + "，正在转发 ...");
        ByteBuf m = (ByteBuf) msg;
        ByteBuf buf = ctx.alloc().buffer(m.readableBytes() * 2);
        buf.writeBytes(m);
        //将数据转发至UDP组播端口
        groupCaster.send(buf);
        //System.out.println("ByteBuf引用计数: " + buf.refCnt());
        buf = null;
        System.out.println("已转发 ...");
    }

    //connect to the server
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //send a message
        System.out.println("已连接到差分码服务器,准备接收差分码 ...");
    }

    //disconnect from the the server
    //断线重连
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与差分码服务器的连接断开,正在尝试重连 ...");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                client.createBootstrap(new Bootstrap(), eventLoop);
            }
        }, 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //        ctx.channel().close();
        System.out.println("始料未及的异常发生...client...");
        cause.printStackTrace();
    }
}
