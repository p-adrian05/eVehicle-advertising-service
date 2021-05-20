package org.example.exceptions;

public class DeleteMessageException extends Exception{

    public DeleteMessageException() {
        super();
    }

    public DeleteMessageException(String message) {
        super(message);
    }
}
