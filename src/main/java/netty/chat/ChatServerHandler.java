package netty.chat;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    // GlobalEventExecutor.INSTANCE是全局的事件执行器，是单例的
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 表示channel处于就绪状态，提示上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        // 客户端的ip
        SocketAddress remoteAddress = channel.remoteAddress();
        // 将该客户的上线提醒加入别的客户端
        // 该方法会将channelGroup中所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端]" + remoteAddress + "上线了" + sdf.format(new Date()) + "\n");
        // 将当前channel加入到channelGroup
        channelGroup.add(channel);
        System.out.println(remoteAddress + " 上线了" + "\n");

    }

    // 表示channel处于下线状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        // 客户端的ip
        SocketAddress remoteAddress = channel.remoteAddress();
        // 将该客户的下线提醒加入别的客户端
        // 该方法会将channelGroup中所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端]" + remoteAddress + "下线了" + sdf.format(new Date()) + "\n");
        System.out.println(remoteAddress + " 下线了" + "\n");
        System.out.println("channelGroup.size=" + channelGroup.size());

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {  
        // 获取当前的channel
        Channel channel = ctx.channel();      
        // 遍历channelGroup中的channel，区分自己和别人
        channelGroup.forEach(ch -> {
            // 不是自己
            if (channel != ch) {
                ch.writeAndFlush("[客户端]" + channel.remoteAddress() + "发送了消息：" + msg + "\n");
            } else {
                ch.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) {
        // 关闭通道
        ctx.close();
    }
}