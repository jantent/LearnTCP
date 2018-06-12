package com.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TcpSocketClient {


    public static void main(String args[]) throws Exception{
        int port = 9817;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入命令：");
        while (true){
            String cmdStr = scanner.nextLine();
            switch (cmdStr) {
                case "conn":
                    socket = new Socket("127.0.0.1", port);
                    in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("已建立连接");
                    break;
                case "send":
                    System.out.println("请输入你需要发送的内容");
                    for (;;) {
                        String sendMsg = scanner.nextLine();
                        switch (sendMsg) {
                            case "exit":
                                System.exit(-1);
                            default:
                                sendMsg(in, out, sendMsg);
                                break;
                        }
                    }
                case "exit":
                    System.exit(-1);
                    break;
                default:
                    break;
            }




        }
    }

    private static void  sendMsg(BufferedReader in,PrintWriter out,String msg) throws Exception{
        out.println(msg);
        String resp = in.readLine();
        System.out.println("服务器："+resp);
    }

    private static void closeAll(BufferedReader in,PrintWriter out,Socket socket){
        if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            in = null;
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            socket = null;
    }

    private static void send() {

//
//        try {
//            socket =
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out = new PrintWriter(socket.getOutputStream(), true);
//            out.println("QUERY TIME ORDER");
//            System.out.println("send order 2 server succeed");
//            String resp = in.readLine();
//            System.out.println("服务器的回复是：" + resp);
//        } catch (Exception e) {
//        } finally {
//            if (out != null) {
//                out.close();
//                out = null;
//            }
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            in = null;
//            if (socket != null)
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            socket = null;
//        }
    }
}
