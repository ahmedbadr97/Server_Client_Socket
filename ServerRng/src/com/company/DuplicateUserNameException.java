package com.company;

public class DuplicateUserNameException extends Exception{
    public DuplicateUserNameException(String message) {
        super(message);
    }
}
