package netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;
    public EchoClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });

        ChannelFuture cf1 = b.connect(host, port).sync();
        ChannelFuture cf2 = b.connect(host, port).sync();
        // 发送消息
        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("Hello, netty !".getBytes()));

        cf1.channel().closeFuture().sync();


        group.shutdownGracefully();
    }
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8765;
        new EchoClient(host, port).start();

    }
}
