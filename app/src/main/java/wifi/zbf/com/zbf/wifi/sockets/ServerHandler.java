package wifi.zbf.com.zbf.wifi.sockets;

import android.util.Log;

import java.net.SocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 服务端读取收到数据
 * Created by hs-301 on 2017/11/13.
 */
public class ServerHandler extends ChannelHandlerAdapter
{
/*    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive!!!!!!!!!!!" + ctx.toString());
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("channelRead!!!!!!!!!!!!!!!!!!!!!!!" + ctx.toString() + msg.toString());
//        ByteBuf buf = (ByteBuf) msg;
        ctx.write(msg);
        //System.out.println(buf.);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("channelReadComplete!!!!!!!!!!!!!!!!!!!!!!!" + ctx.toString() + ctx.toString());
        ctx.flush();
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("exceptionCaught!!!!!!!!!!!!!!!!!!!!!!!" + ctx.toString() + cause.toString());
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }




}
