package org.example.exceptions;

public class UnknownUserException extends Exception{

    public UnknownUserException() {
        super();
    }

    public UnknownUserException(String message) {
        super(message);
    }
}
