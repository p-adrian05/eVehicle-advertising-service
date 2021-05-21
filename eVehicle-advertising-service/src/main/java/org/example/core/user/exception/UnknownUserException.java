package org.example.core.user.exception;

public class UnknownUserException extends Exception{

    public UnknownUserException() {
        super();
    }

    public UnknownUserException(String message) {
        super(message);
    }
}
