package wifi.zbf.com.zbf.wifi.sockets2;

import android.support.v4.util.ArrayMap;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by user on 2016/10/27.
 */

public class Dispatcher extends SimpleChannelInboundHandler<Test.ProtoTest>
{
    private ArrayMap<Integer, OnReceiveListener> receiveListenerHolder;

    public Dispatcher() {
        receiveListenerHolder = new ArrayMap<>();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Test.ProtoTest msg) throws Exception {
        channelRead0(ctx,msg);
    }

    public void holdListener(Test.ProtoTest test, OnReceiveListener onReceiveListener) {
        receiveListenerHolder.put(test.getId(), onReceiveListener);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Test.ProtoTest protoTest) throws Exception {
        if (receiveListenerHolder.containsKey(protoTest.getId())) {
            OnReceiveListener listener = receiveListenerHolder.remove(protoTest.getId());
            if (listener != null) {
                listener.handleReceive(protoTest);
            }
        }
    }
}
