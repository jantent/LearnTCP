package com.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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
                BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                getResponse(in,out,socket);
            }
        }finally {
            if(server != null){
                System.out.println("time server close");
                server.close();
                server = null;
            }
        }
    }

    private static void getResponse(BufferedReader in,PrintWriter out,Socket socket) throws Exception{
        try {
            String response = null;
            String request = null;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                request = in.readLine();
                if (request == null) {
                    break;
                }
                System.out.println("客户端:" + request);
                response = scanner.nextLine();
                out.println(response);
            }
        }catch (Exception e){
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

