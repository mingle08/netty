package netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf)msg;

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String request = new String(data, "utf-8");
        System.out.println("Server received : " + request);

        // 写给client端
        String response = "from server: Hi, client .";
//        ctx.write(Unpooled.copiedBuffer(response.getBytes()));
//        ctx.flush();


//        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()))
//               .addListener(ChannelFutureListener.CLOSE);

        final ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
//        f.addListener(new ChannelFutureListener() {
//           @Override
//           public void operationComplete(ChannelFuture future) throws Exception {
//                assert f == future;
//                ctx.close();
//           }
//        });

        f.addListener(ChannelFutureListener.CLOSE);



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
