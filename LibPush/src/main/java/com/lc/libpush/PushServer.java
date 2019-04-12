package com.lc.libpush;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class PushServer extends Service {

    private final String TAG = "PushServer";

    private Selector mSelector;
    private SocketChannel mSocketChannel;

    private String mIpv4;
    private int mPort;

    private ServerBinder mBinder ;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        mBinder = new ServerBinder(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    class ServerBinder extends Binder {

        public ServerBinder(PushServer server){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    init();
                    return null;
                }
            }.execute();
        }


        public String getServerIp(){
            return mIpv4;
        }

        public int getServerPort(){
            return mPort;
        }
    }




    private void init(){
        mIpv4 = Utils.getIpAddress(getApplicationContext());
        mPort = 8888;
        Log.d(TAG,"server ip:"+mIpv4+",mPort:"+mPort);
        try {
            mSelector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress(mIpv4,mPort);
            serverSocketChannel.socket().bind(addr);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(mSelector,SelectionKey.OP_ACCEPT);
            while(mSelector.select() > 0){
                Set<SelectionKey> selectionKeys = mSelector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if(sk.isAcceptable()){
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        mSocketChannel = socketChannel;

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        String content = "";

                        while(socketChannel.read(byteBuffer) > 0){
                            byteBuffer.flip();
                            content += byteBuffer;
                        }

                        Log.d(TAG,""+content);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
