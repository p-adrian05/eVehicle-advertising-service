package org.example.exceptions;

public class UnknownUserRateException extends Exception{

    public UnknownUserRateException() {
        super();
    }

    public UnknownUserRateException(String message) {
        super(message);
    }
}
