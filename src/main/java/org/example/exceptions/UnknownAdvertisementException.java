package org.example.exceptions;

public class UnknownAdvertisementException extends Exception{

    public UnknownAdvertisementException() {
        super();
    }

    public UnknownAdvertisementException(String message) {
        super(message);
    }
}
