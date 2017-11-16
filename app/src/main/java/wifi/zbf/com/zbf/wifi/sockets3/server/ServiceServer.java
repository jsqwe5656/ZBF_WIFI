package wifi.zbf.com.zbf.wifi.sockets3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Sockets通信服务端
 * Created by hs-301 on 2017/11/16.
 */
public class ServiceServer
{
    private int PORT = 62014;
    private static ServiceServer INSTANCE;


    public ServiceServer() {
    }

    public static ServiceServer getInstance() {
        if (INSTANCE == null)
        {
            synchronized (ServiceServer.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new ServiceServer();
                }
            }
        }
        return INSTANCE;
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new HelloServerInitializer());

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(62014).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

            // 可以简写为
            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
