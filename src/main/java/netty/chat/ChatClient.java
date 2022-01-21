package netty.chat;

import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast("decoder", new StringDecoder());
                     p.addLast("encoder", new StringEncoder());
                     p.addLast(new ChatClientHandler());
                 }
             });
 
            // Start the server.
            ChannelFuture f = b.connect("127.0.0.1", 9000).sync();
            System.out.println("ChatClient 启动完成");
            
            Channel channel = f.channel();
            System.out.println("===" + channel.localAddress() + "===");
            
            // 客户端需要创建一个扫描器用来输入信息
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                // 通过channel发送
                channel.writeAndFlush(msg);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

        
    }
}