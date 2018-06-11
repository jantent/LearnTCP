package com.heartbreak.client;

import com.heartbreak.AbstractHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author tangj
 * @date 2018/6/10 16:18
 */
public class HeartBeatClient {

    private Random random = new Random();
    Channel channel;
    Bootstrap bootstrap;

    String host = "127.0.0.1";
    int port = 9817;

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
                    .handler(new SimpleClientInitializer(HeartBeatClient.this));
            doConncet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData() throws Exception {
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        while (true){
//            channel.writeAndFlush(in.readLine());
//        }
        for (int i = 0; i < 10; i++) {
            if (channel != null && channel.isActive()) {
                String contentMsg = "content msg" + i;
                ByteBuf byteBuf = channel.alloc().buffer();
                byteBuf.writeBytes(contentMsg.getBytes());
                channel.writeAndFlush(byteBuf);
                Thread.sleep(random.nextInt(10000));
            }
        }
    }

    public void doConncet() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (channelFuture.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println("connect server successfully");
                } else {
                    System.out.println("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConncet();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });

    }


    private class SimpleClientInitializer extends ChannelInitializer<SocketChannel> {

        private HeartBeatClient client;

        public SimpleClientInitializer(HeartBeatClient client){
            this.client = client;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new IdleStateHandler(0,0,5));
            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024,0,4,-4,0));
            pipeline.addLast("handler",new HeartBeatClientHandler(client));
        }
    }

    private class HeartBeatClientHandler extends AbstractHandler {

        private HeartBeatClient client;

        public HeartBeatClientHandler(HeartBeatClient client) {
            super("client");
            this.client = client;
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.err.println("客户端与服务端断开连接");
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                   client.doConncet();
                }
            }, 5, TimeUnit.SECONDS);
        }

        @Override
        public void processData(ChannelHandlerContext ctx, ByteBuf byteBuf) {
            byte[] data = new byte[byteBuf.readableBytes() - 5];
            byteBuf.skipBytes(5);
            byteBuf.readBytes(data);
            String content = new String(data);
            System.out.println(name+" get content: "+content);
        }

        @Override
        public void handleAllIdle(ChannelHandlerContext ctx) {
            super.handleAllIdle(ctx);
            sendPingMsg(ctx);
        }
    }
}
