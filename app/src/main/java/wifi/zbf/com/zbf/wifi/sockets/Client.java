package wifi.zbf.com.zbf.wifi.sockets;

import android.os.Handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

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
    private Channel channel ;

    public Client(String ipAdress, int port, Handler handler) {
        this.handler = handler;
        this.ipAdress = ipAdress;
        this.port = port;
    }

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {//配置Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>()
                    {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                            pipeline.addLast(new ClientHandler());
                        }
                    });

            //发起异步连接操作
            ChannelFuture channelFuture = bootstrap.connect(ipAdress, port).sync();

            channel = channelFuture.channel();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } finally
        {
            //关闭，释放线程资源
            group.shutdownGracefully();
        }
    }


    /**
     * 发送信息
     */
    public void sendMessage(final String msg) {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                workerGroup = new NioEventLoopGroup();
                bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>()
                        {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new ClientHandler());
                            }
                        });
                ChannelFuture future = null;
                try
                {
                    future = bootstrap.connect(ipAdress, port).sync();
                    future.channel().writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));

                    future.channel().closeFuture().sync();
                    workerGroup.shutdownGracefully();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }

 /*   public void sendMessage(String msg){//连接成功后，通过Channel提供的接口进行IO操作
        try {
            if (channel != null && channel.isOpen()) {
                channel.writeAndFlush(sendMsg).sync();     //(1)
            } else {
                throw new Exception("channel is null | closed");
            }
        } catch (Exception e) {
            sendReconnectMessage();
            e.printStackTrace();
        }
    }
    @Test
    public void nettyClient(){
        new NettyClient().connect(8989, "localhost");
    }

}*/

}
