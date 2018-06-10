package com.heartbreak.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author tangj
 * @date 2018/6/10 10:46
 */
public class HeartBeatServer {
    private static int port = 9817;
    public HeartBeatServer(int port){
        this.port = port;
    }

    ServerBootstrap bootstrap  = null;
    ChannelFuture f;

    // 检测chanel是否接受过心跳数据时间间隔（单位秒）
    private static final int READ_WAIT_SECONDS = 10;

    public static void main(String args[]){
        HeartBeatServer heartBeatServer = new HeartBeatServer(port);
        heartBeatServer.startServer();
    }

    public void startServer(){
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workergroup = new NioEventLoopGroup();
        try{
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossgroup,workergroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HeartBeatServerInitializer());
            // 服务器绑定端口监听
            f = bootstrap.bind(port).sync();
            System.out.println("server start");
            // 监听服务器关闭监听，此方法会阻塞
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossgroup.shutdownGracefully();
            workergroup.shutdownGracefully();
        }
    }



    private class HeartBeatServerInitializer extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("decoder",new StringDecoder());
            pipeline.addLast("encoder",new StringEncoder());
            /*
             * 这里只监听读操作
             * 可以根据需求，监听写操作和总得操作
             */
            pipeline.addLast("ping",new IdleStateHandler(READ_WAIT_SECONDS,0,0, TimeUnit.SECONDS));

            pipeline.addLast("handler",new HeartbeatHandler());
        }
    }

}
