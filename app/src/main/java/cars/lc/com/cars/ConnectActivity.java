package cars.lc.com.cars;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {

    private Selector mSelector;
    private SocketChannel mSocketChannel;
    private Charset mCharset = Charset.forName("utf-8");

    private Button mConnectBtn;
    private Button mSendBtn;
    private EditText mMsgTv;

    private String ip = "192.168.100.9";
    private  int port = 8888;

    private String TAG = "ConnectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mConnectBtn = findViewById(R.id.connect);
        mSendBtn = findViewById(R.id.send);
        mMsgTv = findViewById(R.id.content);
        mConnectBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
    }


    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Toast.makeText(ConnectActivity.this, "Connect server", Toast.LENGTH_SHORT).show();
            }else if(msg.what == 1){

            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v == mConnectBtn){
            new ConnectThread().start();
        }else if(v == mSendBtn){
            new SendThread().start();
        }
    }


    class SendThread extends Thread{
        @Override
        public void run() {
            super.run();

            try {

                mSocketChannel.write(mCharset.encode(mMsgTv.getText().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ConnectThread extends Thread{
        @Override
        public void run() {
            super.run();

            try {
                mSelector = Selector.open();
                InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
                mSocketChannel = SocketChannel.open(inetSocketAddress);
                updateHandler.sendEmptyMessage(0);
                mSocketChannel.configureBlocking(false);
                mSocketChannel.register(mSelector,SelectionKey.OP_READ);
                while(mSelector != null && mSelector.select() >0){
                    Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while(iterator.hasNext()){
                        SelectionKey sk = iterator.next();
                        iterator.remove();
                        if(sk != null && sk.isReadable()){
                            mSocketChannel = (SocketChannel) sk.channel();
                            ByteBuffer buff = ByteBuffer.allocate(1024);
                            String data = "";
                            while(mSocketChannel.read(buff) > 0){
                                buff.flip();
                                data += mCharset.decode(buff);
                                buff.clear();
                            }
                            Log.d(TAG,"buffer:"+buff.toString());
                            sk.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
