package wifi.zbf.com.zbf.wifi.sockets3.server;

import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by hs-301 on 2017/11/16.
 */

public class HelloServerHandler extends SimpleChannelInboundHandler<String>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        // 收到消息直接打印输出
        System.out.println(channelHandlerContext.channel().remoteAddress() + " Say : " + s);

        // 返回客户端消息 - 我已经接收到了你的消息
        channelHandlerContext.writeAndFlush("Received your message !\n");
    }


    /**
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        super.channelActive(ctx);

    }

}
