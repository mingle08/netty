package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class NioServer {

    static List<SocketChannel> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 创建NIO的ServerSocketChannel，与BIO的ServerSocket类似
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9000));
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        System.out.println("服务端启动成功");

        while(true) {
            // accept方法, NIO模式下 不阻塞，BIO模式下 阻塞
            // NIO模式的非阻塞是由操作系统实现的，底层调用了Linux内核的accept函数
            SocketChannel clientChannel = serverSocketChannel.accept();
            if (clientChannel != null) {
                System.out.println("客户端连接成功");
                // 设置clientChannel为 非阻塞
                clientChannel.configureBlocking(false);
                // 保存客户端连接
                clientList.add(clientChannel);
            }

            // 遍历连接进行数据读取
            Iterator<SocketChannel> iter = clientList.iterator();
            while (iter.hasNext()) {
                SocketChannel client = iter.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                int len = client.read(byteBuffer);
                if (len > 0)
                    System.out.println("接收到消息： " + new String(byteBuffer.array()));
                else if (len == -1) {// 如果客户端断开，把客户端从List中除去
                    iter.remove();
                    System.out.println("客户端断开连接");
                }                
            }
        }
    }
}