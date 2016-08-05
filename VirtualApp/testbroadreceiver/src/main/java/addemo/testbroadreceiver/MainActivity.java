package addemo.testbroadreceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainActivity extends Activity {
    Handler handler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                sendBroadcast(new Intent("com.test"));
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        };
        handler.sendEmptyMessageDelayed(0, 2000);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.test");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIME_TICK");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("sdk", "dym action:" + intent.getAction());
            }
        }, filter);
    }

}