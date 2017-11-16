package wifi.zbf.com.zbf.wifi.sockets2;

import android.util.Log;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import wifi.zbf.com.zbf.wifi.sockets.ServerHandler;
import wifi.zbf.com.zbf.wifi.sockets3.server.HelloServerInitializer;

/**
 * Created by user on 2016/10/27.
 */
public class NettyServer
{
    private ServerBootstrap mServerBootstrap;
    private EventLoopGroup mWorkerGroup;
    private ChannelFuture channelFuture;
    private boolean isInit;

    private static NettyServer INSTANCE;

    public final static int PORT_NUMBER = 62014;

    private NettyServer() {
    }

    public static NettyServer getInstance() {
        if (INSTANCE == null)
        {
            synchronized (NettyServer.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new NettyServer();
                }
            }
        }
        return INSTANCE;
    }

    public void init() {
        if (isInit)
        {
            return;
        }
        //创建worker线程池，这里只创建了一个线程池，使用的是netty的多线程模型
        mWorkerGroup = new NioEventLoopGroup();
        //服务端启动引导类，负责配置服务端信息
        mServerBootstrap = new ServerBootstrap();
        mServerBootstrap.group(mWorkerGroup)
                .channel(NioServerSocketChannel.class)
//                .handler(new ChannelInitializer<NioServerSocketChannel>()
//                {
//                    @Override
//                    protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
//                        ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
//                        pipeline.addLast(new ServerHandler());
//                    }
//                })
                .childHandler(new HelloServerInitializer());
        channelFuture = mServerBootstrap.bind(PORT_NUMBER);
        isInit = true;
        channelFuture.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (channelFuture.isSuccess())
                {
                    Log.e("zbf", "服务器端口开启成功:" + PORT_NUMBER);
                }
            }
        });
    }

    public void shutDown() {
        if (channelFuture != null && channelFuture.isSuccess())
        {
            isInit = false;
            channelFuture.channel().closeFuture();
            mWorkerGroup.shutdownGracefully();
        }
    }
}
