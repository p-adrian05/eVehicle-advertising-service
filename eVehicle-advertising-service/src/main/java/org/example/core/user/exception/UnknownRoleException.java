package org.example.core.user.exception;

public class UnknownRoleException extends Exception{

    public UnknownRoleException() {
        super();
    }

    public UnknownRoleException(String message) {
        super(message);
    }
}
