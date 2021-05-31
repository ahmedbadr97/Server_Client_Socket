package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientGame implements Runnable {
    private final Socket socket;
    PrintWriter outputToSocket;
    BufferedReader inputFromSocket;
    Queue<String> inputFromClientQueue;
    boolean connectionAlive;
    ExecutorService clientGamePool;


    User user;

    public ClientGame(Socket socket) {
        this.socket = socket;
        try {
            outputToSocket = new PrintWriter(socket.getOutputStream(), true);
            inputFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputFromClientQueue = new ConcurrentLinkedDeque<>();
            connectionAlive = true;
            clientGamePool = Executors.newCachedThreadPool();
        } catch (IOException e) {
            System.out.println("Server Error " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if(Main.isAllDead())return;
            login();
            initiateClientInputListenerThread();
            play();
            outputToSocket.println("Game Finished");
            outputToSocket.println(Main.getUsersTable());
            exitTheGame();
        } catch (DuplicateUserNameException | InputMismatchException | IOException e) {
            outputToSocket.println(e.getMessage());
            exitTheGame();

        }
        System.out.println("Client " + socket.getInetAddress() + " Finished his service");

    }

    public void login() throws DuplicateUserNameException, IOException, InputMismatchException {
        String sentMessage;
        User user;
        //Get User login
        System.out.println("Waiting User to login");
        sentMessage = inputFromSocket.readLine();
        System.out.println("User logged in");
        //Parsing Input
        String[] messageWords = sentMessage.split(" ");
        boolean LoginDataIsInvalid = (messageWords.length != 2) || !messageWords[0].toLowerCase().equals("login");
        if (LoginDataIsInvalid) {
            throw new InputMismatchException("Invalid Input \n" +
                    "sign up with your username on the main server using the login <username> command\n" +
                    "e.g.login ahmed");
        }
        user = new User(messageWords[1]);
        //Throws Duplicate UserName if same username found
        Main.addUser(user);
        this.user = user;
        user.setMessageRecivedAction((message) ->
        {
            //this method is invoked when any user sends message to current user
            clientGamePool.execute(() -> outputToSocket.println(message));

        });
        outputToSocket.println("you had successfully logged in\n Welcome " + user.getName());
        outputToSocket.println(Main.getUsersTable());

    }

    public void play() {
        String sentMessage;
        //Process input from inputFromClientQueue
        while (!Main.isAllDead() && connectionAlive) {
            if (!inputFromClientQueue.isEmpty()) {
                sentMessage = inputFromClientQueue.poll();
                String[] messageWords = sentMessage.split(" ");
                if (messageWords.length == 0) continue;
                if (messageWords.length == 2 && messageWords[0].equals("shoot") && Main.userExists(messageWords[1])) {
                    if (user.isAlive()) {
                        User shootedUser = Main.getUserFromData(messageWords[1]);
                        if (shootedUser == user)
                            outputToSocket.println("You cant shoot your self");
                        else user.shoot(shootedUser);
                    } else
                        outputToSocket.println("you cant shoot any one ,you are dead");
                } else if (messageWords[0].equals("players")) {
                    outputToSocket.println(Main.getUsersTable());
                } else outputToSocket.println("Invalid Command!");
            }

        }
    }

    public void initiateClientInputListenerThread() {
        clientGamePool.execute(() -> {
            while (connectionAlive&&!Main.isAllDead()) {
                try {
                    inputFromClientQueue.add(inputFromSocket.readLine());
                } catch (IOException e) {
                    break;
                }
            }

        });

    }

    public void exitTheGame() {
        try {
            outputToSocket.println("quit");
            connectionAlive = false;
            outputToSocket.close();
            inputFromSocket.close();
            socket.close();
            clientGamePool.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
