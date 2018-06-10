package com.heartbreak.client;

import com.heartbreak.server.HeartBeatServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author tangj
 * @date 2018/6/10 16:18
 */
public class HeartBeatClient {

    Random random = new Random(System.currentTimeMillis());
    Channel channel = null;
    Bootstrap bootstrap;

    private String host = "127.0.0.1";
    private int port = 9817;

    public static void main(String args[]) throws Exception {
        HeartBeatClient client = new HeartBeatClient();
        client.run();
        Thread.sleep(3000);
        client.sendData();
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleClientInitializer());
            doConnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel = future.channel();
                    System.out.println("connect to server successfully!");
                } else {
                    System.out.println("Failed to connect to server, try connect after 10s");
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });

    }

    public void sendData() throws Exception {

        for (int i = 0; i < 10; i++) {
            if (channel != null && channel.isActive()) {
                //  BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            while (true){
//                channel.writeAndFlush(in.readLine());
//            }
                String content = "content msg " + i;
                ByteBuf buf = channel.alloc().buffer();
                buf.writeBytes(content.getBytes());
                channel.writeAndFlush(buf);
                int sleeptime = random.nextInt(10000);
                System.out.println("发送间隔为："+sleeptime+" 发送消息为： "+content);
                Thread.sleep(sleeptime);
            }
        }
    }

    private class SimpleClientInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder", new StringEncoder());
        }
    }
}
