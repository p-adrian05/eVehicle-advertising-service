package org.example.config;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Builder
public class ErrorInfo {

    public final Timestamp timestamp;
    public final int status;
    public final String error;
    public final String message;
    public final String path;

}
