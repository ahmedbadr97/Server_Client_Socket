package com.company;

import java.util.concurrent.locks.ReentrantLock;

public class User {
    final private String name;
    private int score;
    private boolean alive;
    private static int noOfDeadUsers = 0;
    private static ReentrantLock userScoreDataLock;
    private static ReentrantLock userInboxLock;
    private MessageRecivedAction messageRecivedAction;

    @FunctionalInterface
    interface MessageRecivedAction {
        void action(String message);
    }

    public User(String name) {
        this.name = name;
        score = 0;
        alive = true;
        userScoreDataLock = new ReentrantLock();
        userInboxLock = new ReentrantLock();

    }

    public void shoot(User opponent) {
        userScoreDataLock.lock();

        if (opponent.isAlive()) {
            this.incScore();
            opponent.alive = false;
            opponent.decScore();
            broadCast(opponent.name + " is Dead");
            noOfDeadUsers++;
            if (noOfDeadUsers == Main.usersData.size() - 1) {
                Main.allDead();
            }
        } else
            this.decScore();
        userScoreDataLock.unlock();
    }

    private void incScore() {
        userScoreDataLock.lock();
        score++;
        userScoreDataLock.unlock();

    }

    private void decScore() {
        userScoreDataLock.lock();
        score--;
        userScoreDataLock.unlock();

    }

    public boolean isAlive() {
        return alive;
    }

    public String getName() {
        return name;
    }

    public void addMessagetoUserInbox(String message) {
        userInboxLock.lock();
        if (messageRecivedAction != null)
            messageRecivedAction.action(message);
        userInboxLock.unlock();

    }

    public static void broadCast(String message) {
        for (User user : Main.usersData.values()) {
            user.addMessagetoUserInbox(message);
        }

    }

    public void setMessageRecivedAction(MessageRecivedAction messageRecivedAction) {
        this.messageRecivedAction = messageRecivedAction;
    }

    public int getScore() {
        return score;
    }
}
