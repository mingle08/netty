package netty.tcpPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


public class ServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String request = (String)msg;
        System.out.println("Server : " + request);
        // 分隔符
//        String response = "from server: " + msg + "$_";
        // 定长
        String response = request;
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
