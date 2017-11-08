package wifi.zbf.com.zbf.wifi;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
            serverSocket = new ServerSocket(port,0);
        } catch (IOException e)
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
                //阻塞，等待设备连接
                socket = serverSocket.accept();
                Message message = Message.obtain();
//                message.what = MainActivity.DEVICE_CONNECTING;
                handler.sendMessage(message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
