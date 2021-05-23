package org.example.core.message.exception;

public class UnknownMessageException extends Exception{

    public UnknownMessageException() {
        super();
    }

    public UnknownMessageException(String message) {
        super(message);
    }
}
