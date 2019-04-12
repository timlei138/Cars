package com.lc.libpush;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class PingHandler extends Handler {

    private Context mContext;

    private String mIpv4;
    private int mPort;
    private SocketChannel mClientChannel;

    //private IntervalList mSendList = new IntervalList();
    private Callback mCallback;

    public PingHandler(Looper looper,Context context){
        super(looper);
        mContext = context;
        //sendEmptyMessage()
    }


    public void setCallback(Callback callback){
        this.mCallback = callback;
    }

    public SocketChannel getConnectChannel(){
        return mClientChannel;
    }


    public void startConnect(String ip,int port){

        mIpv4 = ip;
        mPort = port;
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }

    private boolean connectServer() throws IOException {
        if(Utils.isNetworkAvailable(mContext)){
            if(mClientChannel == null){
                if(!TextUtils.isEmpty(mIpv4)){
                    mClientChannel = SocketChannel.open();
                    mClientChannel.configureBlocking(false);
                    if(!mClientChannel.connect(new InetSocketAddress(mIpv4,mPort))){
                        long now = System.currentTimeMillis();
                        while(!mClientChannel.finishConnect());
                        if(mCallback != null){
                            mCallback.onConnect(mIpv4,mPort);
                        }

                    }
                }
            }else{
                mClientChannel.isConnected();
            }
        }
        return false;
    }

    private void closeChannel(){
        if(mClientChannel != null){
            try {
                mClientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mClientChannel = null;
    }

    private void send(){
        boolean isSend = false;

    }

    private void receive(){

    }

    public interface Callback{
        void onConnect(String ip,int port);
        void onDisconncet(String reasion,String ip,int port);
    }
}
