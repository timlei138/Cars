package com.lc.server;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class LibServer {

    private String TAG = "LibServer";
    private String mIpv4;
    private int mPort;
    private Selector mSelector;
    private SocketChannel mSocketChannel;
    private ServerSocketChannel mServerChannel;
    private Charset mCharset = Charset.forName("UTF-8");


    public static void main(String[] args){

        LibServer server = new LibServer();
        server.setIpAddress("192.168.100.9",8888);
        boolean status = server.startServer();
        System.out.println("start Server "+ (status ? "success" : "fail"));
    }



    public void setIpAddress(String ip,int port){
        mIpv4 = ip;
        mPort = port;

    }

    public boolean startServer(){
        if(mIpv4 == null || mIpv4.equals("")){
            System.out.println("IP Address is Null !");
            return false;
        }
        try {
            mSelector = Selector.open();
            //通过 open 方式打开一个未绑定的ServerSocketChannel实例
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(mIpv4,mPort);
            //绑定IP
            serverSocketChannel.socket().bind(inetSocketAddress);
            //非阻塞方式工作
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(mSelector,SelectionKey.OP_ACCEPT);
            while(mSelector.select()>0){
                // 依次处理selector上的每个已选择的SelectionKey
                Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
                //这里必须用iterator，如果用for遍历Set程序会报错
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey sk = iterator.next();
                    // 从selector上的已选择的SelectionKey集合中删除正在处理的SelectionKey
                    iterator.remove();
                    // 如果sk对应的通道包含客户端的连接请求
                    if (sk.isAcceptable()) {
                        // 调用accept方法接受连接，产生服务端对应的SocketChannel
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("client accept-->IP："+socketChannel.getRemoteAddress()+"\n");
                        // 设置采用非阻塞模式
                        socketChannel.configureBlocking(false);
                        mSocketChannel = socketChannel;
                        // 将该SocketChannel也注册到selector
                        socketChannel.register(mSelector, SelectionKey.OP_READ);
                        // 将sk对应的Channel设置成准备接受其他请求
                        sk.interestOps(SelectionKey.OP_ACCEPT);
                    }
                    // 如果sk对应的通道有数据需要读取
                    if(sk.isReadable()){
                        // 获取该SelectionKey对应的Channel，该Channel中有可读的数据
                        SocketChannel socketChannel = (SocketChannel) sk.channel();
                        mSocketChannel = socketChannel;
                        // 定义准备执行读取数据的ByteBuffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        String content = "";
                        // 开始读取数据
                        while(socketChannel.read(buffer)>0){
                            buffer.flip();
                            content += mCharset.decode(buffer);
                        }
                        System.out.println("content->"+content);

                        //处理命令

                        sk.interestOps(SelectionKey.OP_READ);
                    }

                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }



}
