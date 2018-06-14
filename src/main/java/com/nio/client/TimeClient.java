package com.nio.client;

/**
 * @author tangj
 * @date 2018/6/14 22:56
 */
public class TimeClient {
    public static void main(String args[]){
        new Thread(new TimeClientHandle("127.0.0.1",9816)).start();
    }
}
