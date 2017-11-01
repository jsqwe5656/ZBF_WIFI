package wifi.zbf.com.zbf.wifi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewInit()
        wifiInit()
    }

    /**
     * 初始化WIFI
     */
    private fun wifiInit(){

    }

    /**
     * 试图组件初始化
     */
    private fun viewInit() {
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}
