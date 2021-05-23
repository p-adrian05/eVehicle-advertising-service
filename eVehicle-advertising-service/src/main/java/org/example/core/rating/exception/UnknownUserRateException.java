package org.example.core.rating.exception;

public class UnknownUserRateException extends Exception{

    public UnknownUserRateException() {
        super();
    }

    public UnknownUserRateException(String message) {
        super(message);
    }
}
