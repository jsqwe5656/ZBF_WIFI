package wifi.zbf.com.zbf.wifi.sockets2;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * Created by user on 2016/10/27.
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<Test.ProtoTest>
{
    private static final String TAG = "ServerChannelHandler";

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Test.ProtoTest protoTest) throws Exception {
        Log.d(TAG, "channelRead0: " + channelHandlerContext.name());
        Test.ProtoTest res = Test.ProtoTest.newBuilder()
                .setId(protoTest.getId())
                .setTitle("res" + protoTest.getTitle())
                .setContent("res" + protoTest.getContent())
                .build();
        channelHandlerContext.writeAndFlush(res);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Test.ProtoTest msg) throws Exception {
        channelRead0(ctx, msg);
    }
}
