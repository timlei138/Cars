package cars.lc.com.cars;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lc.libpush.PushServer;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"service Connect:"+name.toShortString());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"service onServiceDisconnected:");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), PushServer.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
