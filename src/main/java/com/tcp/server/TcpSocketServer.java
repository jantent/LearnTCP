package com.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSocketServer {
    public static void main(String args[]) throws Exception{
        int port = 9818;
        ServerSocket server = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Socket socket = null;
        try{
            server = new ServerSocket(port);
            System.out.println("tcp socket server start ... port:"+port);
            for (;;){
                socket = server.accept();
                executorService.execute(new TcpSocketServerHandler(socket));
            }
        }finally {
            if(server != null){
                System.out.println("time server close");
                server.close();
                server = null;
            }
        }
    }

}

class TcpSocketServerHandler implements Runnable {
    private Socket socket;
    TcpSocketServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // 定义输出流
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            // 客户端发送消息
            String request = null;
            // 服务端返回响应
            String response = null;
            while (true) {
                request = in.readLine();
                if (request == null) {
                    break;
                }
                System.out.println("客户端建立连接,Socket ID :"+socket.toString());
                response = "adfs";
                out.println(response);
            }
        } catch (Exception e) {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            socket = null;

        }

    }
}
