package wifi.zbf.com.zbf.wifi.sockets2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import wifi.zbf.com.zbf.wifi.sockets3.server.ServiceServer;


/**
 * 运行netty服务端的服务
 * Created by user on 2016/10/27.
 */
public class ServerService extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NettyServer.getInstance().init();
//        ServiceServer.getInstance().init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NettyServer.getInstance().shutDown();
    }
}
