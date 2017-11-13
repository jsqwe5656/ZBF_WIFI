package wifi.zbf.com.zbf.wifi.sockets;

import android.os.Handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 连接客户端
 * Created by hs-301 on 2017/11/13.
 */
public class Client
{
    private Handler handler;
    private String ipAdress;
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    public Client(String ipAdress, int port, Handler handler)
    {
        this.handler = handler;
        this.ipAdress = ipAdress;
        this.port = port;
        clientInit();
    }

    /**
     * 初始化客户端
     */
    private void clientInit()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                workerGroup = new NioEventLoopGroup();
                bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>()
                        {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception
                            {
                                socketChannel.pipeline().addLast(new ClientHandler());
                            }
                        });
            }
        }).start();
    }

    /**
     * 发送信息
     */
    public boolean sendMessage(String msg)
    {
        ChannelFuture future = null;
        try
        {
            future = bootstrap.connect(ipAdress, port).sync();
            future.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes()));
            future.channel().closeFuture().sync();
            workerGroup.shutdownGracefully();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }


}
