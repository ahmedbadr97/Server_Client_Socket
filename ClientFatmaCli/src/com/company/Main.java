package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
   static boolean connectionIsAlive;
    static Socket socket;
   static BufferedReader inputFromSocket;
   static PrintWriter outputToSocket;
    static Scanner inputFromCl;
    static Queue<String> inputFromCliQueue;
   static Thread inputFromCliThread;

    public static void main(String[] args) {
        try {
               inputFromCl=new Scanner(System.in);
            socket=new Socket("127.0.0.1",5000);
            outputToSocket = new PrintWriter(socket.getOutputStream(), true);
            inputFromSocket =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputToSocket.flush();
            connectionIsAlive=true;
            inputFromCliQueue=new ConcurrentLinkedDeque<>();
            //address of the host we want to connect to and the port number
            runMessageListener();
            runCliInputListener();
            while (connectionIsAlive)
            {
                if(!inputFromCliQueue.isEmpty())
                    outputToSocket.println(inputFromCliQueue.poll());
            }
            System.exit(1);

        }
        catch (IOException e)
        {
            System.out.println("Client Error "+e.getMessage() );
        }
    }
    public static void runMessageListener()
    {
        new Thread(() -> {
            String input;
            while (connectionIsAlive &&!socket.isClosed())
            {
                try {
                    input=inputFromSocket.readLine();
                    if(input.equals("quit"))
                    {
                        connectionIsAlive =false;
                        break;
                    }

                    System.out.println(input);
                } catch (IOException e) {
                    break;
                }
            }
            closeConnection();
        }).start();

    }
    public static void runCliInputListener()
    {
       inputFromCliThread=  new Thread(()->{
           while (connectionIsAlive&&!Thread.interrupted())
           {
               if(inputFromCl.hasNext())
                   inputFromCliQueue.add(inputFromCl.nextLine());
           }

        });
      inputFromCliThread.start();
    }
    public static void closeConnection(){
        if(!socket.isClosed()) {
            try {
                inputFromCliThread.interrupt();
                inputFromCl.close();
                outputToSocket.close();
                inputFromSocket.close();
                socket.close();
                connectionIsAlive=false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
