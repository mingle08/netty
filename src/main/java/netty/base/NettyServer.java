package netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    /**
     * 1，启动main方法
     * 2，使用windows自带的telnet客户端来充当客户端的角色
     * （1）输入 telnet localhost 9000
     * （2）按组合键ctrl + ]
     * （3）输入 send hello
     * 3，在idea控制台能看到日志：收到客户端的消息：hello
     *
     * 注意，如果不按组合键ctrl + ]，输入的单词都是单个字符发过来的
     * @param args
     */
    public static void main(String[] args) {
        // 创建2个线程组：bossGroup, workerGroup，含有的子线程NioEventLoop的个数默认是cpu核数的2倍
        // bossGroup只处理连接请求，workerGroup处理真正的客户端业务
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        EventLoopGroup workerGroup = new NioEventLoopGroup(100);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>(){      //为accept channel的pipeline预添加的inboundhandler
                        @Override     //当新连接accept的时候，这个方法会调用
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MyChannelHandler());   //为当前的channel的pipeline添加自定义的处理函数
                        }

                    });
            System.out.println("netty server start...");
            // 绑定一个端口，并且同步，生成一个ChannelFuture异步对象
            ChannelFuture cf = serverBootstrap.bind(9000).sync();

        } catch (Exception e) {

        }
    }
}
