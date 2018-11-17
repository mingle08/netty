package netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;
    public EchoServer(int port){
        this.port = port;
    }


    public void start() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        // 1. 第1个线程组，是用于接收Client端连接的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 2. 第2个线程组， 是用于实际的业务处理操作的
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{

            // 3.创建一个辅助类BootStrap，就是对我们的Server进行一系列的配置
            ServerBootstrap b = new ServerBootstrap();
            // 4. 把二个线程组加入进来
            b.group(bossGroup, workerGroup)
                    // 指定使用NioServerSocketChannel这种类型的通道
                    .channel(NioServerSocketChannel.class)
                    // localAddress
                    .localAddress(new InetSocketAddress(port))
                    // 一定要使用childHandler去绑定具体的事件处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                          @Override
                          protected void initChannel(SocketChannel ch) throws Exception {
                              ch.pipeline().addLast(echoServerHandler);
                          }
                    })
                    /**
                     * 服务器TCP内核模块维护2个队列，我们称之为A，B吧
                     * client端向server端connect的时候，会发送带有SYN标志的包（第一次握手）
                     * server端收到client端发来的SYN时，向client端发送SYN ACK确认（第二次握手）
                     * 此时TCP内核模块把client端连接加入到A队列中，然后Server端收到client端发来的ACK时（第三次握手）
                     * TCP内核模块把client端连接从A队列移到B队列，连接完成，应用程序的accept会返回。
                     * 也就是说accept从B队列中取出完成三次握手的连接
                     * A队列和B队列的长度之和是backlog。当A，B队列的长度之和大于backlog时，新连接将会被TCP内核拒绝。
                     * 所以，如果backlog过小，可能会出现accept速度跟不上，A，B队列满了，导致新的client无法连接。
                     * 要注意的是：backlog对程序支持的连接数并无影响，backlog影响的只是还没有被accept取出的连接
                     */
                    // 设置TCP缓冲区，可以不设置，默认就是128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定指定的端口，进行监听
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        int port = 8765;
        new EchoServer(port).start();
    }

}
