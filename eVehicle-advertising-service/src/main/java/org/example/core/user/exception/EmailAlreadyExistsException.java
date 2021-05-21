package org.example.core.user.exception;

public class EmailAlreadyExistsException extends Exception{

    public EmailAlreadyExistsException() {
        super();
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
