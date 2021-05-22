package org.example.core.advertising.exception;

public class UnknownCategoryException extends Exception{

    public UnknownCategoryException() {
        super();
    }

    public UnknownCategoryException(String message) {
        super(message);
    }
}
