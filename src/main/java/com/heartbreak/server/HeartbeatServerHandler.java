package com.heartbreak.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Random;

/**
 * @author tangj
 * @date 2018/6/10 12:21
 */
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<String> {
    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0;

    // 定义服务端没有收到心跳消息的最大次数
    private static final int MAX_UN_REC_PING_TIMES = 3;

    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg!=null && msg.equals("Heartbeat")){
            System.out.println("客户端"+ctx.channel().remoteAddress()+"--心跳信息--");
        }else {
            System.out.println("客户端----请求消息----："+msg);
            String resp = "商品的价格是："+random.nextInt(1000);
            ctx.writeAndFlush(resp);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()==IdleState.READER_IDLE){
                System.out.println("===服务端===(READER_IDLE 读超时)");
                // 失败计数器次数大于等于3次的时候，关闭链接，等待client重连
                if (unRecPingTimes >= MAX_UN_REC_PING_TIMES) {
                    System.out.println("===服务端===(读超时，关闭chanel)");
                    // 连续超过N次未收到client的ping消息，那么关闭该通道，等待client重连
                    ctx.close();
                } else {
                    // 失败计数器加1
                    unRecPingTimes++;
                }
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("一个客户端已连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("一个客户端已断开连接");
    }
}
