package org.example.exceptions;

public class UnknownCategoryException extends Exception{

    public UnknownCategoryException() {
        super();
    }

    public UnknownCategoryException(String message) {
        super(message);
    }
}
