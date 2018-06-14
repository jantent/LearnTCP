package com.nio;

public class TimeServer {

    public static void main(String args[]){
        int port = 9816;
        MutipleexerTimeServer timeServer = new MutipleexerTimeServer(port);
        new Thread(timeServer,"nit").start();
    }
}
