package org.example.config;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Builder
public class ExceptionResponse {

    public final Timestamp timestamp;
    public final String message;
    public final String details;

}
