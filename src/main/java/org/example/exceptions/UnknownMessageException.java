package org.example.exceptions;

public class UnknownMessageException extends Exception{

    public UnknownMessageException() {
        super();
    }

    public UnknownMessageException(String message) {
        super(message);
    }
}
