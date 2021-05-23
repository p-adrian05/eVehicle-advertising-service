package org.example.config;

import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
public class ValidationErrorInfo extends ErrorInfo {

    public List<String> errors;

    public ValidationErrorInfo(Timestamp timestamp, int status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }
}
