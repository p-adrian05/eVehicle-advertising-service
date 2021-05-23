package org.example.core.rating.exception;

public class UserRateAlreadyExistsException extends Exception{

    public UserRateAlreadyExistsException() {
        super();
    }

    public UserRateAlreadyExistsException(String message) {
        super(message);
    }
}
