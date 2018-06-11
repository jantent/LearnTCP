package com.heartbreak;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class AbstractHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private int heartbeatCount = 0;

    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0;

    // 定义服务端没有收到心跳消息的最大次数
    private static final int MAX_UN_REC_PING_TIMES = 3;

    public AbstractHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg.getByte(4) == PING_MSG) {
            sendPongMsg(ctx);
        } else if (msg.getByte(4) == PING_MSG) {
            System.out.println(name + " get pong msg from " + ctx.channel().remoteAddress());
        } else {
            processData(ctx, msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is active---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    public void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE---");
    }

    public void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    public void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }

    public void sendPingMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PING_MSG);
        context.channel().writeAndFlush(buf);
        heartbeatCount++;
        System.out.println(name + " send ping msg to" + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

    public void sendPongMsg(ChannelHandlerContext context) {
        ByteBuf byteBuf = context.alloc().buffer(5);
        byteBuf.writeInt(5);
        byteBuf.writeByte(PONG_MSG);
        context.channel().writeAndFlush(byteBuf);
        heartbeatCount++;
        System.out.println(name + " send pong msg to" + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

    abstract public void processData(ChannelHandlerContext ctx, ByteBuf byteBuf);
}
