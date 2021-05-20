package org.example.exceptions;

public class UnknownRoleException extends Exception{

    public UnknownRoleException() {
        super();
    }

    public UnknownRoleException(String message) {
        super(message);
    }
}
