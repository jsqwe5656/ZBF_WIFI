package wifi.zbf.com.zbf.wifi.sockets3.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *
 * Created by hs-301 on 2017/11/16.
 */
public class SocketsClient
{
    private String IP_ADRESS;
    private int PORT;
    private static SocketsClient INSTANCE;

    public SocketsClient() {

    }

    public static SocketsClient getINstance() {
        if (INSTANCE == null)
        {
            synchronized (SocketsClient.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new SocketsClient();
                }
            }
        }
        return INSTANCE;
    }

    public void init(String ipAdress,int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HelloClientInitializer());

            // 连接服务端
            Channel ch = b.connect(ipAdress, port).sync().channel();

/*            // 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; )
            {
                String line = in.readLine();
                if (line == null)
                {
                    continue;
                }
                *//*
                  * 向服务端发送在控制台输入的文本 并用"\r\n"结尾
                  * 之所以用\r\n结尾 是因为我们在handler中添加了 DelimiterBasedFrameDecoder 帧解码。
                  * 这个解码器是一个根据\n符号位分隔符的解码器。所以每条消息的最后必须加上\n否则无法识别和解码
                 * *//*
                ch.writeAndFlush(line + "\r\n");
            }*/
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }

    }


}
