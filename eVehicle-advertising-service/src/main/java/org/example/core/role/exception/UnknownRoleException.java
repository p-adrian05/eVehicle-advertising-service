package org.example.core.role.exception;

public class UnknownRoleException extends Exception{

    public UnknownRoleException() {
        super();
    }

    public UnknownRoleException(String message) {
        super(message);
    }
}
