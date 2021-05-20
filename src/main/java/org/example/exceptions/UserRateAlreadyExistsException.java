package org.example.exceptions;

public class UserRateAlreadyExistsException extends Exception{

    public UserRateAlreadyExistsException() {
        super();
    }

    public UserRateAlreadyExistsException(String message) {
        super(message);
    }
}
