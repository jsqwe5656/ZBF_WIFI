package wifi.zbf.com.zbf.wifi.sockets;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import wifi.zbf.com.zbf.wifi.Main2Activity;
import wifi.zbf.com.zbf.wifi.PublicStatics;

/**
 * 监听线程
 * Created by hs-301 on 2017/11/8.
 */
public class ListenerThread extends Thread
{
    private ServerSocket serverSocket = null;
    private Handler handler;
    private int port;
    private Socket socket;

    public ListenerThread(int port, Handler handler) {
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        try
        {
            serverSocket = new ServerSocket(port);
//            Main2Activity.connectThread.start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true)
        {
            try
            {
                Message message = Message.obtain();
                message.what = PublicStatics.DEVICE_CONNECTING;
                handler.sendMessage(message);
                //阻塞，等待设备连接
                socket = serverSocket.accept();
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
