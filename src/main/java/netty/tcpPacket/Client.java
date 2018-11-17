package netty.tcpPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

public class Client {

    private final String host;
    private final int port;
    public Client(String host, int port){
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
                        // 设置特殊分隔符
//                        ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
//                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
                        // 设置定长字符串接收
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(5));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        ChannelFuture cf1 = b.connect(host, port).sync();
        // 发送消息
        // 分隔符
//        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("aaa$_".getBytes()));
//        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("bbbbb$_".getBytes()));
//        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("ccccccc$_".getBytes()));

        // 定长
        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("aaaaabbb".getBytes()));
        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("bb".getBytes()));
        cf1.channel().writeAndFlush(Unpooled.copiedBuffer("cccccc".getBytes()));


        cf1.channel().closeFuture().sync();


        group.shutdownGracefully();
    }
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8769;
        new Client(host, port).start();

    }
}
