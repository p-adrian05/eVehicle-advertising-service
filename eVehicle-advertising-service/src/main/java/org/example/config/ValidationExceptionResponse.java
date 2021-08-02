package org.example.config;

import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
public class ValidationExceptionResponse extends ExceptionResponse {

    public List<String> errors;

    public ValidationExceptionResponse(Timestamp timestamp, String message, String details) {
        super(timestamp,  message, details);
    }
}
