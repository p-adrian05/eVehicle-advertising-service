package org.example.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends Exception{

    public ValidationException() {
        super();
    }

    private List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
