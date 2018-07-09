package com.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteBufTest {
    public static void main(String args[]){
        // 在内存池中申请 直接内存
        ByteBuf directByteBuf = ByteBufAllocator.DEFAULT.directBuffer(1024);
        // 在内存池中申请 堆内存
        ByteBuf heapByteBuf = ByteBufAllocator.DEFAULT.heapBuffer(1024);

    }
}
