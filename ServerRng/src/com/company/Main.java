package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static ReentrantLock serverDataLock;
    static HashMap<String, User> usersData;
    static private boolean allDead = false;
    static ExecutorService clientThreadsPool;

    public static void main(String[] args) throws DuplicateUserNameException, InterruptedException {
        clientThreadsPool = Executors.newCachedThreadPool();
        serverDataLock = new ReentrantLock();
        usersData = new HashMap<>();
        // ___________Test data ______//
//        addUser(new User("mohamed"));
//        addUser(new User("khaled"));
        addUser(new User("a"));
        //___________________________//
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            startServerClientListener(serverSocket);
            while (!isAllDead())
            {
                Thread.sleep(10);
            }
            // Dummy user to terminate ClientListener
            System.out.println("Game Finished");
            Socket dummyUser = new Socket("127.0.0.1", 5000);
            // Finish Current Threads
            clientThreadsPool.shutdownNow();
            dummyUser.close();
        } catch (IOException e) {
            System.out.println("server excection " + e.getMessage());
        }
    }

    public static void addUser(User user) throws DuplicateUserNameException {
        serverDataLock.lock();
        if (usersData.containsKey(user.getName()))
            throw new DuplicateUserNameException("This username:" + user.getName() + " already taken by another user");
        else {
            usersData.put(user.getName().toLowerCase(), user);

        }
        serverDataLock.unlock();
    }

    public static String getUsersTable() {
        StringBuilder table = new StringBuilder();
        table.append("---------------------------\n" +
                "|username|status|score|\n" +
                "---------------------------\n");
        for (User user : usersData.values()) {
            table.append("|").append(user.getName()).append(" | ")
                    .append(user.isAlive() ? "alive" : "dead").
                    append(" | ").append(user.getScore()).append(" |\n");

        }
        table.append("---------------------------");
        return table.toString();

    }

    public static User getUserFromData(String name) {
        return usersData.get(name.toLowerCase());
    }

    public static boolean userExists(String user) {
        return usersData.containsKey(user.toLowerCase());
    }
    public static void allDead()
    {

        allDead=true;

    }

    public static boolean isAllDead() {
        return allDead;
    }

    public static void startServerClientListener(ServerSocket serverSocket) {
        clientThreadsPool.execute(() -> {
            Socket client = null;
            while (!isAllDead()) {
                try {

                    client = serverSocket.accept();
                    if (!isAllDead())
                        clientThreadsPool.execute(new ClientGame(client));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
