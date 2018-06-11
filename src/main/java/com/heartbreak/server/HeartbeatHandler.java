package com.heartbreak.server;

import com.heartbreak.AbstractHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class HeartbeatHandler extends AbstractHandler {
    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0;
    private String userid;

    // 定义服务端没有收到心跳消息的最大次数
    private static final int MAX_UN_REC_PING_TIMES = 3;

    public HeartbeatHandler() {
        super("server");
    }

    @Override
    public void processData(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes()-5];
        ByteBuf responseBuf = Unpooled.copiedBuffer(byteBuf);
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        System.out.println(name+" get content: "+content);
        ctx.writeAndFlush(responseBuf);
    }
    @Override
    public void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
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
