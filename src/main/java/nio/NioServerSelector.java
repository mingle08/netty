package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

public class NioServerSelector {

    static List<SocketChannel> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 创建NIO的ServerSocketChannel，与BIO的ServerSocket类似
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9000));
        // 打开selector
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端启动成功");

        while(true) {
            // 阻塞等待需要处理的事件发生
            selector.select();

            // 获取selector中注册的全部事件的selectionKey实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            // 遍历连接进行数据读取
            while (iter.hasNext()) {
                SelectionKey selectionKey = iter.next();
                // 如果是OP_ACCEPT，就进行连接获取和注册   
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    // 可以注册读事件和写事件，这里只注册了读事件
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if (selectionKey.isReadable()) {
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = client.read(byteBuffer);
                    if (len > 0) {
                        System.out.println("接收到消息： " + new String(byteBuffer.array()));
                    } else if(len == -1) {// 如果客户端断开，把客户端从List中除去
                        iter.remove();
                        System.out.println("客户端断开连接");
                    }
                }
                // 从集合中删除本次处理过的事件
                iter.remove();
            }
            
        }
    }
}