package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MutipleexerTimeServer implements Runnable{

    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop = false;

    /**
     * 创建多路复用器，绑定NIO端口
     *
     * @param port
     */
    public MutipleexerTimeServer(int port){
        try{
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port),1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time server start at port: "+port );
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handlerInput(key);
                    }catch (Exception e){
                        if (key !=null){
                            key.cancel();
                            if (key.channel() !=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 多路复用器关闭后，所有注册到上面的channel和pipe等资源都不被自动去注册并关闭，所有不需要重复释放资源
        if (selector!=null){
            try {
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void handlerInput(SelectionKey key) throws Exception{
        if (key.isValid()){
            // 处理新接入的请求消息
            if (key.isAcceptable()){
                // Accept the new Connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // Add the new connection to the selector
                sc.register(selector,SelectionKey.OP_READ);
            }

            if (key.isReadable()){
                // Read the data
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes>0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("server receive order: "+body);
                    String correntTime = "QUERY".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
                    doWrite(sc,correntTime);
                }else if (readBytes<0){
                     // 对端链路关闭
                    key.cancel();
                    sc.close();
                }else {
                    ;
                }
            }
        }
    }

    private void doWrite(SocketChannel channel,String response) throws Exception{
        if (response!=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
