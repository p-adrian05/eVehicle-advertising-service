package org.example.exceptions;

public class UnknownImageException extends Exception{

    public UnknownImageException() {
        super();
    }

    public UnknownImageException(String message) {
        super(message);
    }
}
