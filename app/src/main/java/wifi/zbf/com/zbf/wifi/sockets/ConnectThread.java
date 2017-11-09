package wifi.zbf.com.zbf.wifi.sockets;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import wifi.zbf.com.zbf.wifi.PublicStatics;

/**
 * 连接线程
 * Created by hs-301 on 2017/11/7.
 */
public class ConnectThread extends Thread
{
    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectThread(Socket socket, Handler handler) {
        setName("ConnectThread");
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        if (socket == null)
        {
            return;
        }
        handler.sendEmptyMessage(PublicStatics.DEVICE_CONNECTED);
        try
        {
            //获取数据流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            while (true)
            {
                //读取数据
                bytes = inputStream.read(buffer);
                if (bytes > 0)
                {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);

                    Message message = Message.obtain();
                    message.what = PublicStatics.GET_MSG;

                    Bundle bundle = new Bundle();
                    bundle.putString("MSG", new String(data));
                    bundle.putString("IP", socket.getInetAddress() + "");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */
    public void sendData(String msg) {
        if (outputStream != null)
        {
            try
            {
                outputStream.write(msg.getBytes());
                Message message = Message.obtain();
                message.what = PublicStatics.SEND_MSG_SUCCSEE;
                Bundle bundle = new Bundle();
                bundle.putString("MSG", new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (IOException e)
            {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = PublicStatics.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG", new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }
}
