package org.example.config;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.exception.CreateAdvertisementException;
import org.example.core.advertising.exception.MaximumSavedAdsReachedException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.image.exception.UnknownImageException;
import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.security.exception.AuthException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
        EmailAlreadyExistsException.class,
        UsernameAlreadyExistsException.class,
        UserRateAlreadyExistsException.class,
        FileUploadException.class})
    public ResponseEntity<Object> handleUnprocessableEntityExists(HttpServletRequest req, Exception ex) {
        return new ResponseEntity<>(ExceptionResponse.builder()
            .timestamp(new Timestamp(new Date().getTime()))
            .message(ex.getMessage())
            .details(req.getRequestURI())
            .build(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {
        UnknownUserException.class,
        UnknownRoleException.class,
        UnknownAdvertisementException.class,
        UnknownCategoryException.class,
        UnknownImageException.class,
        UnknownUserRateException.class,
        UnknownMessageException.class})
    public ResponseEntity<Object> handleNotFound(HttpServletRequest req, Exception ex) {
        return new ResponseEntity<>(ExceptionResponse.builder()
            .timestamp(new Timestamp(new Date().getTime()))
            .message(ex.getMessage())
            .details(req.getRequestURI())
            .build(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {DeleteMessageException.class, UpdateMessageException.class,
        CreateAdvertisementException.class,
        MaximumSavedAdsReachedException.class, IllegalArgumentException.class, AuthException.class})
    public ResponseEntity<Object> handleDeleteUpdateMessageException(HttpServletRequest req, Exception ex) {
        return new ResponseEntity<>(ExceptionResponse.builder()
            .timestamp(new Timestamp(new Date().getTime()))
            .message(ex.getMessage())
            .details(req.getRequestURI())
            .build(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest req) {
        List<String> messages = ex.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
        ValidationExceptionResponse validationExceptionResponse =
            new ValidationExceptionResponse(new Timestamp(new Date().getTime()), "Argument validation failed",
                ((ServletWebRequest)req).getRequest().getRequestURI());
        validationExceptionResponse.setErrors(messages);
        return new ResponseEntity<>(validationExceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
