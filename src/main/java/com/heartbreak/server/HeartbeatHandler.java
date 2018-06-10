package com.heartbreak.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * https://segmentfault.com/a/1190000006931568#articleHeader1
 * @author tangj
 * @date 2018/6/10 12:21
 */
@ChannelHandler.Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<String> {
    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0;
    private String userid;

    // 定义服务端没有收到心跳消息的最大次数
    private static final int MAX_UN_REC_PING_TIMES = 3;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        System.out.println("客户端发来消息："+msg);


    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
             /*读超时*/
                System.out.println("===服务端===(READER_IDLE 读超时)");
                // 失败计数器次数大于等于3次的时候，关闭链接，等待client重连
                if (unRecPingTimes >= MAX_UN_REC_PING_TIMES) {
                    System.out.println("===服务端===(读超时，关闭chanel)");
                    // 连续超过N次未收到client的ping消息，那么关闭该通道，等待client重连
                    ctx.channel().close();
                } else {
                    // 失败计数器加1
                    unRecPingTimes++;
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                /*写超时*/
                System.out.println("===服务端===(WRITER_IDLE 写超时)");
            } else if (event.state() == IdleState.ALL_IDLE) {
                /*总超时*/
                System.out.println("===服务端===(ALL_IDLE 总超时)");
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
